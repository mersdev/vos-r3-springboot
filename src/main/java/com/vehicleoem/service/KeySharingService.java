package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.repository.DigitalKeyRepository;
import com.vehicleoem.repository.VehicleRepository;
import com.vehicleoem.repository.KeySharingInvitationRepository;
import com.vehicleoem.exception.*;
import com.vehicleoem.dto.ShareKeyRequest;
import com.vehicleoem.dto.ShareKeyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class KeySharingService {
    
    @Autowired
    private DigitalKeyRepository digitalKeyRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private KeySharingInvitationRepository invitationRepository;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private NotificationService notificationService;
    
    public ShareKeyResponse shareKey(ShareKeyRequest request) {
        // Find the vehicle
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
            .orElseThrow(() -> new VehicleNotFoundException(request.getVehicleVin()));
        
        // Validate sharing permissions
        validateKeySharingPermissions(vehicle, request);
        
        // Check if invitation already exists
        if (invitationRepository.existsByVehicleAndFriendEmailAndStatus(
                vehicle, request.getFriendEmail(), InvitationStatus.PENDING)) {
            throw new KeyManagementException("Invitation already sent to this email for this vehicle");
        }
        
        // Create sharing invitation
        KeySharingInvitation invitation = createSharingInvitation(vehicle, request);
        invitation = invitationRepository.save(invitation);
        
        // Send invitation notification
        notificationService.sendFriendKeyInvitation(
            request.getFriendEmail(), 
            request.getFriendName(), 
            vehicle.getOwner(), 
            null // Will be created when invitation is accepted
        );
        
        // Log audit trail
        auditService.logVehicleAction(vehicle.getVin(), "KEY_SHARING_INVITATION_SENT", 
            request.getSharedBy(), "Key sharing invitation sent to " + request.getFriendEmail());
        
        return new ShareKeyResponse(true, "Invitation sent successfully", invitation.getInvitationCode());
    }
    
    public DigitalKey acceptKeyInvitation(String invitationCode, String deviceId, String deviceOem) {
        // Find invitation
        KeySharingInvitation invitation = invitationRepository.findByInvitationCode(invitationCode)
            .orElseThrow(() -> new KeyManagementException("Invalid invitation code"));
        
        // Validate invitation
        validateInvitation(invitation);
        
        // Create the friend digital key
        DigitalKey friendKey = createFriendKey(invitation, deviceId, deviceOem);
        friendKey = digitalKeyRepository.save(friendKey);
        
        // Update invitation status
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitation.setDigitalKey(friendKey);
        invitationRepository.save(invitation);
        
        // Update vehicle key count
        Vehicle vehicle = invitation.getVehicle();
        vehicle.incrementKeyCount();
        vehicleRepository.save(vehicle);
        
        // Send confirmation notifications
        notificationService.sendKeyCreatedNotification(vehicle.getOwner(), friendKey);
        
        // Log audit trail
        auditService.logKeyAction(friendKey.getKeyId(), "FRIEND_KEY_ACCEPTED", 
            invitation.getFriendEmail(), "Friend key invitation accepted");
        
        return friendKey;
    }
    
    public void revokeSharedKey(String keyId, String revokedBy, String reason) {
        DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
        
        if (digitalKey.getKeyType() != KeyType.FRIEND) {
            throw new KeyManagementException("Can only revoke friend keys through sharing service");
        }
        
        // Revoke the key
        digitalKey.revoke(revokedBy, reason);
        digitalKeyRepository.save(digitalKey);
        
        // Update vehicle key count
        Vehicle vehicle = digitalKey.getVehicle();
        vehicle.decrementKeyCount();
        vehicleRepository.save(vehicle);
        
        // Update invitation status if exists
        invitationRepository.findByDigitalKey(digitalKey).ifPresent(invitation -> {
            invitation.setStatus(InvitationStatus.REVOKED);
            invitation.setRevokedAt(LocalDateTime.now());
            invitation.setRevokedBy(revokedBy);
            invitation.setRevocationReason(reason);
            invitationRepository.save(invitation);
        });
        
        // Send notification to friend
        if (digitalKey.getFriendEmail() != null) {
            notificationService.sendKeyRevokedNotification(digitalKey.getFriendEmail(), 
                digitalKey.getFriendName(), vehicle, reason);
        }
        
        // Log audit trail
        auditService.logKeyAction(keyId, "FRIEND_KEY_REVOKED", revokedBy, reason);
    }
    
    public void updateKeyPermissions(String keyId, PermissionLevel newPermissionLevel, String updatedBy) {
        DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
        
        if (digitalKey.getKeyType() != KeyType.FRIEND) {
            throw new KeyManagementException("Can only update permissions for friend keys");
        }
        
        PermissionLevel oldPermissionLevel = digitalKey.getPermissionLevel();
        digitalKey.setPermissionLevel(newPermissionLevel);
        digitalKeyRepository.save(digitalKey);
        
        // Send notification to friend
        if (digitalKey.getFriendEmail() != null) {
            notificationService.sendPermissionUpdateNotification(digitalKey.getFriendEmail(), 
                digitalKey.getFriendName(), digitalKey.getVehicle(), oldPermissionLevel, newPermissionLevel);
        }
        
        // Log audit trail
        auditService.logKeyAction(keyId, "PERMISSION_UPDATED", updatedBy, 
            "Permission changed from " + oldPermissionLevel + " to " + newPermissionLevel);
    }
    
    public void setKeyRestrictions(String keyId, String timeRestrictions, String locationRestrictions, 
                                  Long maxUsageCount, String updatedBy) {
        DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
        
        if (digitalKey.getKeyType() != KeyType.FRIEND) {
            throw new KeyManagementException("Can only set restrictions for friend keys");
        }
        
        digitalKey.setTimeRestrictions(timeRestrictions);
        digitalKey.setLocationRestrictions(locationRestrictions);
        digitalKey.setMaxUsageCount(maxUsageCount);
        digitalKeyRepository.save(digitalKey);
        
        // Send notification to friend
        if (digitalKey.getFriendEmail() != null) {
            notificationService.sendKeyRestrictionsUpdateNotification(digitalKey.getFriendEmail(), 
                digitalKey.getFriendName(), digitalKey.getVehicle());
        }
        
        // Log audit trail
        auditService.logKeyAction(keyId, "RESTRICTIONS_UPDATED", updatedBy, 
            "Key restrictions updated");
    }
    
    public List<KeySharingInvitation> getPendingInvitations(String vehicleVin) {
        Vehicle vehicle = vehicleRepository.findByVin(vehicleVin)
            .orElseThrow(() -> new VehicleNotFoundException(vehicleVin));
        
        return invitationRepository.findByVehicleAndStatus(vehicle, InvitationStatus.PENDING);
    }
    
    public List<DigitalKey> getSharedKeys(String vehicleVin) {
        return digitalKeyRepository.findByVehicleVinAndKeyType(vehicleVin, KeyType.FRIEND);
    }
    
    private void validateKeySharingPermissions(Vehicle vehicle, ShareKeyRequest request) {
        // Check subscription allows friend keys
        if (!vehicle.canCreateFriendKeys()) {
            throw SubscriptionException.friendKeysNotAllowed();
        }
        
        // Check key limits
        validationService.validateKeyLimits(vehicle);
        
        // Validate email
        validationService.validateEmail(request.getFriendEmail());
        
        // Check if owner is trying to share with themselves
        if (vehicle.getOwner().getEmail().equalsIgnoreCase(request.getFriendEmail())) {
            throw new KeyManagementException("Cannot share key with vehicle owner");
        }
        
        // Validate expiration date
        if (request.getExpiresAt() != null && request.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
        
        // Check subscription tier limits for expiration
        if (request.getExpiresAt() != null) {
            LocalDateTime maxAllowedExpiration = LocalDateTime.now()
                .plusDays(vehicle.getSubscriptionTier().getKeyExpirationDays());
            if (request.getExpiresAt().isAfter(maxAllowedExpiration)) {
                throw new IllegalArgumentException("Expiration date exceeds subscription tier limit");
            }
        }
    }
    
    private KeySharingInvitation createSharingInvitation(Vehicle vehicle, ShareKeyRequest request) {
        KeySharingInvitation invitation = new KeySharingInvitation();
        invitation.setVehicle(vehicle);
        invitation.setFriendEmail(request.getFriendEmail());
        invitation.setFriendName(request.getFriendName());
        invitation.setFriendPhone(request.getFriendPhone());
        invitation.setPermissionLevel(request.getPermissionLevel());
        invitation.setExpiresAt(request.getExpiresAt());
        invitation.setTimeRestrictions(request.getTimeRestrictions());
        invitation.setLocationRestrictions(request.getLocationRestrictions());
        invitation.setMaxUsageCount(request.getMaxUsageCount());
        invitation.setSharedBy(request.getSharedBy());
        invitation.setInvitationCode(generateInvitationCode());
        invitation.setInvitationExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days to accept
        invitation.setStatus(InvitationStatus.PENDING);
        
        return invitation;
    }
    
    private void validateInvitation(KeySharingInvitation invitation) {
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new KeyManagementException("Invitation is no longer valid");
        }
        
        if (invitation.getInvitationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new KeyManagementException("Invitation has expired");
        }
        
        // Check if vehicle subscription is still valid
        validationService.validateVehicleSubscription(invitation.getVehicle());
        
        // Check if vehicle still has capacity for more keys
        validationService.validateKeyLimits(invitation.getVehicle());
    }
    
    private DigitalKey createFriendKey(KeySharingInvitation invitation, String deviceId, String deviceOem) {
        DigitalKey friendKey = new DigitalKey();
        friendKey.setKeyId(generateKeyId());
        friendKey.setDeviceId(deviceId);
        friendKey.setDeviceOem(deviceOem);
        friendKey.setKeyType(KeyType.FRIEND);
        friendKey.setVehicle(invitation.getVehicle());
        friendKey.setFriendEmail(invitation.getFriendEmail());
        friendKey.setFriendName(invitation.getFriendName());
        friendKey.setFriendPhone(invitation.getFriendPhone());
        friendKey.setPermissionLevel(invitation.getPermissionLevel());
        friendKey.setExpiresAt(invitation.getExpiresAt());
        friendKey.setTimeRestrictions(invitation.getTimeRestrictions());
        friendKey.setLocationRestrictions(invitation.getLocationRestrictions());
        friendKey.setMaxUsageCount(invitation.getMaxUsageCount());
        friendKey.activate();
        
        return friendKey;
    }
    
    private String generateInvitationCode() {
        return "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private String generateKeyId() {
        return "FRIEND-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
