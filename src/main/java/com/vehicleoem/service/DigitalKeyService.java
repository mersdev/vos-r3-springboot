package com.vehicleoem.service;

import com.vehicleoem.dto.*;
import com.vehicleoem.model.*;
import com.vehicleoem.repository.*;
import com.vehicleoem.client.*;
import com.vehicleoem.security.CryptographyService;
import com.vehicleoem.security.CertificateService;
import com.vehicleoem.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
public class DigitalKeyService {

    @Autowired
    private DigitalKeyRepository digitalKeyRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private OwnerAccountRepository ownerAccountRepository;

    @Autowired
    private KeyTrackingClient keyTrackingClient;

    @Autowired
    private DeviceOemClient deviceOemClient;

    @Autowired
    private VehicleTelematicsClient vehicleTelematicsClient;

    @Autowired
    private CryptographyService cryptographyService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AuditService auditService;

    @CircuitBreaker(name = "trackKey", fallbackMethod = "trackKeyFallback")
    @Retry(name = "trackKey")
    public TrackKeyResponse trackKey(TrackKeyRequest request) {
        try {
            // Find vehicle first for validation
            Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(request.getVehicleId()));

            // Comprehensive validation
            validationService.validateTrackKeyRequest(request, vehicle);
            validationService.validateVehicleSubscription(vehicle);
            validationService.validateKeyLimits(vehicle);
            validationService.validateOwnerAccount(vehicle.getOwner());

            // Check if key already exists (idempotency)
            Optional<DigitalKey> existingKey = digitalKeyRepository.findByKeyId(request.getKeyId());
            if (existingKey.isPresent()) {
                return new TrackKeyResponse(true, "Key already tracked", request.getKeyId(),
                    existingKey.get().getKeyId());
            }

            // Create digital key with business logic
            DigitalKey digitalKey = createDigitalKeyWithBusinessLogic(request, vehicle);

            // Save to database
            digitalKey = digitalKeyRepository.save(digitalKey);

            // Update vehicle key count
            vehicle.incrementKeyCount();
            vehicleRepository.save(vehicle);

            // Register with KTS
            registerWithKTS(digitalKey);

            // Send event notification
            eventNotificationService.sendKeyTrackedNotification(digitalKey);

            // Log audit trail
            auditService.logKeyAction(digitalKey.getKeyId(), "TRACK_KEY", "SYSTEM",
                "Key tracked for vehicle " + vehicle.getVin());

            return new TrackKeyResponse(true, "Key tracked successfully",
                digitalKey.getKeyId(), digitalKey.getKeyId());

        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions to be handled by global handler
        } catch (Exception e) {
            throw new BusinessException("TRACK_KEY_ERROR", "Failed to track key: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "manageKey", fallbackMethod = "manageKeyFallback")
    @Retry(name = "manageKey")
    public ManageKeyResponse manageKey(ManageKeyRequest request) {
        try {
            // Validate action
            validationService.validateKeyAction(request.getAction());

            // Find digital key
            DigitalKey digitalKey = digitalKeyRepository.findByKeyId(request.getKeyId())
                .orElseThrow(() -> KeyManagementException.keyNotFound(request.getKeyId()));

            // Validate key status transition
            validationService.validateKeyStatusTransition(digitalKey, request.getAction());

            // Validate vehicle subscription is still active for key operations
            validationService.validateVehicleSubscription(digitalKey.getVehicle());

            // Process action with business logic
            processKeyActionWithBusinessLogic(digitalKey, request.getAction(), request.getReason(), request.getRequestedBy());

            // Save changes
            digitalKeyRepository.save(digitalKey);

            // Update vehicle key count if key was terminated
            if (digitalKey.getStatus() == KeyStatus.TERMINATED) {
                Vehicle vehicle = digitalKey.getVehicle();
                vehicle.decrementKeyCount();
                vehicleRepository.save(vehicle);
            }

            // Send telematics command to vehicle
            sendVehicleCommand(digitalKey, request.getAction());

            // Update KTS
            updateKTSStatus(digitalKey);

            // Send event notification
            eventNotificationService.sendKeyStatusChangedNotification(digitalKey, request.getAction());

            // Log audit trail
            auditService.logKeyAction(digitalKey.getKeyId(), "MANAGE_KEY_" + request.getAction().toUpperCase(),
                request.getRequestedBy() != null ? request.getRequestedBy() : "SYSTEM", request.getReason());

            return new ManageKeyResponse(true, "Key managed successfully",
                digitalKey.getKeyId(), digitalKey.getStatus().name());

        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            throw new BusinessException("MANAGE_KEY_ERROR", "Failed to manage key: " + e.getMessage(), e);
        }
    }



    private DigitalKey createDigitalKeyWithBusinessLogic(TrackKeyRequest request, Vehicle vehicle) {
        DigitalKey digitalKey = new DigitalKey();
        digitalKey.setKeyId(request.getKeyId());
        digitalKey.setDeviceId(request.getDeviceId());
        digitalKey.setDeviceOem(request.getDeviceOem());
        digitalKey.setKeyType(KeyType.valueOf(request.getKeyType().toUpperCase()));
        digitalKey.setVehicle(vehicle);
        digitalKey.setPublicKey(request.getPublicKey());
        digitalKey.setUiBundle(request.getUiBundle());
        digitalKey.setVehicleMobilizationData(request.getVehicleMobilizationData());
        digitalKey.setFriendEmail(request.getFriendEmail());

        if (request.getExpiresAt() != null && !request.getExpiresAt().trim().isEmpty()) {
            digitalKey.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(),
              DateTimeFormatter.ISO_DATE_TIME));
        }

        // Set business logic based on subscription tier and key type
        SubscriptionTier tier = vehicle.getSubscriptionTier();

        // Set expiration based on subscription tier if not provided
        if (digitalKey.getExpiresAt() == null) {
            digitalKey.setExpiresAt(LocalDateTime.now().plusDays(tier.getKeyExpirationDays()));
        }

        // Set permission level based on key type
        if (digitalKey.getKeyType() == KeyType.FRIEND) {
            digitalKey.setPermissionLevel(PermissionLevel.DRIVE_ONLY); // Default for friend keys
        } else {
            digitalKey.setPermissionLevel(PermissionLevel.FULL_ACCESS); // Owner keys get full access
        }

        // Set valid from date
        digitalKey.setValidFrom(LocalDateTime.now());

        // Activate the key
        digitalKey.activate();

        return digitalKey;
    }

    private void registerWithKTS(DigitalKey digitalKey) {
        try {
            KeyRegistrationRequest ktsRequest = new KeyRegistrationRequest();
            ktsRequest.setKeyId(digitalKey.getKeyId());
            ktsRequest.setVehicleId(digitalKey.getVehicle().getVin());
            ktsRequest.setDeviceId(digitalKey.getDeviceId());
            ktsRequest.setKeyType(digitalKey.getKeyType().name());
            ktsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            keyTrackingClient.registerKey(ktsRequest);
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Failed to register key with KTS: " + e.getMessage());
        }
    }

    private void processKeyActionWithBusinessLogic(DigitalKey digitalKey, String action, String reason, String requestedBy) {
        String upperAction = action.toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        switch (upperAction) {
            case "TERMINATE":
                digitalKey.revoke(requestedBy, reason);
                break;
            case "SUSPEND":
                digitalKey.setStatus(KeyStatus.SUSPENDED);
                digitalKey.setUpdatedAt(now);
                break;
            case "RESUME":
                // Additional business validation for resume
                if (digitalKey.isExpired()) {
                    throw KeyManagementException.keyExpired(digitalKey.getKeyId());
                }
                if (digitalKey.isUsageLimitReached()) {
                    throw KeyManagementException.usageLimitReached(digitalKey.getKeyId());
                }
                digitalKey.setStatus(KeyStatus.ACTIVE);
                digitalKey.setUpdatedAt(now);
                break;
            case "EXPIRE":
                digitalKey.setStatus(KeyStatus.EXPIRED);
                digitalKey.setExpiresAt(now);
                digitalKey.setUpdatedAt(now);
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    private void sendVehicleCommand(DigitalKey digitalKey, String action) {
        try {
            TelematicsCommandRequest telematicsRequest = new TelematicsCommandRequest();
            telematicsRequest.setVehicleId(digitalKey.getVehicle().getVin());
            telematicsRequest.setCommand(action);
            telematicsRequest.setKeyId(digitalKey.getKeyId());
            telematicsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            vehicleTelematicsClient.sendCommand(telematicsRequest);
        } catch (Exception e) {
            System.err.println("Failed to send vehicle command: " + e.getMessage());
        }
    }

    private void updateKTSStatus(DigitalKey digitalKey) {
        try {
            KeyStatusUpdateRequest ktsRequest = new KeyStatusUpdateRequest();
            ktsRequest.setKeyId(digitalKey.getKeyId());
            ktsRequest.setNewStatus(digitalKey.getStatus().name());
            ktsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            keyTrackingClient.updateKeyStatus(ktsRequest);
        } catch (Exception e) {
            System.err.println("Failed to update KTS status: " + e.getMessage());
        }
    }

    // Fallback methods
    public TrackKeyResponse trackKeyFallback(TrackKeyRequest request, Exception ex) {
        return new TrackKeyResponse(false, "Service temporarily unavailable: " + ex.getMessage(),
          request.getKeyId(), null);
    }

    public ManageKeyResponse manageKeyFallback(ManageKeyRequest request, Exception ex) {
        return new ManageKeyResponse(false, "Service temporarily unavailable: " + ex.getMessage(),
          request.getKeyId(), null);
    }
}
