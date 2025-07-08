package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Entity
@Table(name = "key_sharing_invitations")
public class KeySharingInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @NotBlank
    @Email
    @Column(name = "friend_email", length = 100)
    private String friendEmail;
    
    @Column(name = "friend_name", length = 100)
    private String friendName;
    
    @Column(name = "friend_phone", length = 20)
    private String friendPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level")
    private PermissionLevel permissionLevel = PermissionLevel.DRIVE_ONLY;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "time_restrictions")
    private String timeRestrictions;
    
    @Column(name = "location_restrictions")
    private String locationRestrictions;
    
    @Column(name = "max_usage_count")
    private Long maxUsageCount;
    
    @NotBlank
    @Column(name = "shared_by", length = 100)
    private String sharedBy;
    
    @NotBlank
    @Column(name = "invitation_code", unique = true, length = 50)
    private String invitationCode;
    
    @Column(name = "invitation_expires_at")
    private LocalDateTime invitationExpiresAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvitationStatus status = InvitationStatus.PENDING;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "revoked_by", length = 100)
    private String revokedBy;
    
    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;
    
    @OneToOne
    @JoinColumn(name = "digital_key_id")
    private DigitalKey digitalKey;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public KeySharingInvitation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.sentAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public String getFriendName() { return friendName; }
    public void setFriendName(String friendName) { this.friendName = friendName; }
    
    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }
    
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getTimeRestrictions() { return timeRestrictions; }
    public void setTimeRestrictions(String timeRestrictions) { this.timeRestrictions = timeRestrictions; }
    
    public String getLocationRestrictions() { return locationRestrictions; }
    public void setLocationRestrictions(String locationRestrictions) { this.locationRestrictions = locationRestrictions; }
    
    public Long getMaxUsageCount() { return maxUsageCount; }
    public void setMaxUsageCount(Long maxUsageCount) { this.maxUsageCount = maxUsageCount; }
    
    public String getSharedBy() { return sharedBy; }
    public void setSharedBy(String sharedBy) { this.sharedBy = sharedBy; }
    
    public String getInvitationCode() { return invitationCode; }
    public void setInvitationCode(String invitationCode) { this.invitationCode = invitationCode; }
    
    public LocalDateTime getInvitationExpiresAt() { return invitationExpiresAt; }
    public void setInvitationExpiresAt(LocalDateTime invitationExpiresAt) { this.invitationExpiresAt = invitationExpiresAt; }
    
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
    
    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }
    
    public String getRevocationReason() { return revocationReason; }
    public void setRevocationReason(String revocationReason) { this.revocationReason = revocationReason; }
    
    public DigitalKey getDigitalKey() { return digitalKey; }
    public void setDigitalKey(DigitalKey digitalKey) { this.digitalKey = digitalKey; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean isExpired() {
        return invitationExpiresAt != null && invitationExpiresAt.isBefore(LocalDateTime.now());
    }
    
    public boolean isPending() {
        return status == InvitationStatus.PENDING && !isExpired();
    }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
