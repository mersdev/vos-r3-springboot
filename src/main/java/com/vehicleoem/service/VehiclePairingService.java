package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.repository.VehicleRepository;
import com.vehicleoem.repository.PairingSessionRepository;
import com.vehicleoem.security.CryptographyService;
import com.vehicleoem.security.CertificateService;
import com.vehicleoem.exception.*;
import com.vehicleoem.dto.InitiatePairingRequest;
import com.vehicleoem.dto.InitiatePairingResponse;
import com.vehicleoem.dto.CompletePairingRequest;
import com.vehicleoem.dto.CompletePairingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
public class VehiclePairingService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PairingSessionRepository pairingSessionRepository;
    
    @Autowired
    private CryptographyService cryptographyService;
    
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private ValidationService validationService;
    
    public InitiatePairingResponse initiatePairing(InitiatePairingRequest request) {
        // Find vehicle
        Vehicle vehicle = vehicleRepository.findByVin(request.getVin())
            .orElseThrow(() -> new VehicleNotFoundException(request.getVin()));
        
        // Validate vehicle can be paired
        validateVehiclePairingEligibility(vehicle);
        
        // Validate owner account
        validationService.validateOwnerAccount(vehicle.getOwner());
        
        // Check for existing active pairing sessions
        pairingSessionRepository.findByVehicleAndStatus(vehicle, PairingStatus.ACTIVE)
            .ifPresent(session -> {
                throw new BusinessException("PAIRING_IN_PROGRESS", 
                    "Vehicle already has an active pairing session");
            });
        
        // Generate pairing credentials
        String pairingPassword = cryptographyService.generatePairingPassword();
        String pairingVerifier = cryptographyService.generatePairingVerifier(pairingPassword);
        
        // Generate ephemeral key pair for this pairing session
        KeyPair ephemeralKeyPair = cryptographyService.generateECKeyPair();
        
        // Create pairing session
        PairingSession pairingSession = new PairingSession();
        pairingSession.setVehicle(vehicle);
        pairingSession.setSessionId(generateSessionId());
        pairingSession.setPairingPassword(pairingPassword);
        pairingSession.setPairingVerifier(pairingVerifier);
        pairingSession.setVehiclePublicKey(Base64.getEncoder().encodeToString(ephemeralKeyPair.getPublic().getEncoded()));
        pairingSession.setVehiclePrivateKey(Base64.getEncoder().encodeToString(ephemeralKeyPair.getPrivate().getEncoded()));
        pairingSession.setStatus(PairingStatus.INITIATED);
        pairingSession.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // 15-minute timeout
        pairingSession.setInitiatedBy(request.getInitiatedBy());
        pairingSession = pairingSessionRepository.save(pairingSession);
        
        // Update vehicle with pairing credentials
        vehicle.setPairingPassword(pairingPassword);
        vehicle.setPairingVerifier(pairingVerifier);
        vehicleRepository.save(vehicle);
        
        // Send pairing verifier to vehicle via telematics
        provisionPairingCredentials(vehicle, pairingVerifier, pairingSession.getSessionId());
        
        // Log audit trail
        auditService.logVehicleAction(vehicle.getVin(), "PAIRING_INITIATED", 
            request.getInitiatedBy(), "Vehicle pairing session initiated");
        
        return new InitiatePairingResponse(true, "Pairing initiated successfully", 
            pairingSession.getSessionId(), pairingPassword, 
            Base64.getEncoder().encodeToString(ephemeralKeyPair.getPublic().getEncoded()));
    }
    
    public CompletePairingResponse completePairing(CompletePairingRequest request) {
        // Find pairing session
        PairingSession pairingSession = pairingSessionRepository.findBySessionId(request.getSessionId())
            .orElseThrow(() -> new BusinessException("PAIRING_SESSION_NOT_FOUND", 
                "Pairing session not found"));
        
        // Validate pairing session
        validatePairingSession(pairingSession);
        
        // Verify pairing password
        if (!pairingSession.getPairingPassword().equals(request.getPairingPassword())) {
            // Increment failed attempts
            pairingSession.incrementFailedAttempts();
            pairingSessionRepository.save(pairingSession);
            
            // Lock session after 3 failed attempts
            if (pairingSession.getFailedAttempts() >= 3) {
                pairingSession.setStatus(PairingStatus.FAILED);
                pairingSessionRepository.save(pairingSession);
                
                auditService.logSecurityEvent("PAIRING_SESSION", pairingSession.getSessionId(), 
                    "PAIRING_LOCKED", "SYSTEM", "Pairing session locked due to failed attempts");
            }
            
            throw new BusinessException("INVALID_PAIRING_PASSWORD", "Invalid pairing password");
        }
        
        // Validate device certificate if provided
        if (request.getDeviceCertificate() != null) {
            validateDeviceCertificate(request.getDeviceCertificate());
        }
        
        // Generate cross-signed certificate for the device
        String crossSignedCertificate = null;
        if (request.getDeviceCertificate() != null) {
            crossSignedCertificate = certificateService.generateDeviceOemCrossSignedCertificate(
                request.getDeviceCertificate());
        }
        
        // Complete the pairing
        pairingSession.setStatus(PairingStatus.COMPLETED);
        pairingSession.setCompletedAt(LocalDateTime.now());
        pairingSession.setDeviceId(request.getDeviceId());
        pairingSession.setDeviceOem(request.getDeviceOem());
        pairingSession.setDevicePublicKey(request.getDevicePublicKey());
        pairingSession.setDeviceCertificate(request.getDeviceCertificate());
        pairingSession.setCrossSignedCertificate(crossSignedCertificate);
        pairingSessionRepository.save(pairingSession);
        
        // Update vehicle with successful pairing
        Vehicle vehicle = pairingSession.getVehicle();
        vehicle.setLastActivityAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vehicle.getVin(), "PAIRING_COMPLETED", 
            request.getDeviceId(), "Vehicle pairing completed successfully");
        
        return new CompletePairingResponse(true, "Pairing completed successfully", 
            crossSignedCertificate, pairingSession.getVehiclePublicKey());
    }
    
    public void revokePairingSession(String sessionId, String revokedBy, String reason) {
        PairingSession pairingSession = pairingSessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new BusinessException("PAIRING_SESSION_NOT_FOUND", 
                "Pairing session not found"));
        
        pairingSession.setStatus(PairingStatus.REVOKED);
        pairingSession.setRevokedAt(LocalDateTime.now());
        pairingSession.setRevokedBy(revokedBy);
        pairingSession.setRevocationReason(reason);
        pairingSessionRepository.save(pairingSession);
        
        // Log audit trail
        auditService.logSecurityEvent("PAIRING_SESSION", sessionId, "PAIRING_REVOKED", 
            revokedBy, "Pairing session revoked: " + reason);
    }
    
    private void validateVehiclePairingEligibility(Vehicle vehicle) {
        if (vehicle.getVehicleStatus() != VehicleStatus.ACTIVE) {
            throw new BusinessException("VEHICLE_NOT_ACTIVE", 
                "Vehicle must be in active status for pairing");
        }
        
        if (!vehicle.isSubscriptionValid()) {
            throw SubscriptionException.expired();
        }
        
        if (vehicle.getPublicKeyCertificate() == null) {
            throw new BusinessException("VEHICLE_CERTIFICATE_MISSING", 
                "Vehicle public key certificate is required for pairing");
        }
    }
    
    private void validatePairingSession(PairingSession pairingSession) {
        if (pairingSession.getStatus() != PairingStatus.INITIATED) {
            throw new BusinessException("INVALID_PAIRING_STATUS", 
                "Pairing session is not in initiated status");
        }
        
        if (pairingSession.isExpired()) {
            throw new BusinessException("PAIRING_SESSION_EXPIRED", 
                "Pairing session has expired");
        }
        
        if (pairingSession.getFailedAttempts() >= 3) {
            throw new BusinessException("PAIRING_SESSION_LOCKED", 
                "Pairing session is locked due to failed attempts");
        }
    }
    
    private void validateDeviceCertificate(String deviceCertificate) {
        try {
            // In a real implementation, this would validate the certificate chain,
            // check expiration, verify against trusted CAs, etc.
            byte[] certBytes = Base64.getDecoder().decode(deviceCertificate);
            
            // Basic validation - certificate should not be empty
            if (certBytes.length == 0) {
                throw new BusinessException("INVALID_DEVICE_CERTIFICATE", 
                    "Device certificate is empty");
            }
            
            // Additional validations would go here...
            
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_DEVICE_CERTIFICATE", 
                "Device certificate is not valid Base64");
        }
    }
    
    private void provisionPairingCredentials(Vehicle vehicle, String verifier, String sessionId) {
        try {
            // In a real implementation, this would send the verifier to the vehicle
            // via the telematics system
            System.out.println("Provisioning pairing verifier to vehicle " + vehicle.getVin() + 
                ": " + verifier + " (Session: " + sessionId + ")");
        } catch (Exception e) {
            System.err.println("Failed to provision pairing credentials: " + e.getMessage());
        }
    }
    
    private String generateSessionId() {
        return "PAIR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
