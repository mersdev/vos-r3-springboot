package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "digital_keys")
public class DigitalKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true, length = 100)
    @Pattern(regexp = "^[A-Za-z0-9-_]{10,100}$", message = "Key ID must be 10-100 characters, alphanumeric with hyphens and underscores")
    private String keyId;

    @NotBlank
    @Column(name = "device_id", length = 100)
    @Size(min = 5, max = 100, message = "Device ID must be between 5 and 100 characters")
    private String deviceId;

    @NotBlank
    @Column(name = "device_oem", length = 50)
    @Size(min = 2, max = 50, message = "Device OEM must be between 2 and 50 characters")
    private String deviceOem;

    @Column(name = "device_model", length = 50)
    private String deviceModel;

    @Column(name = "device_os", length = 30)
    private String deviceOs;

    @Column(name = "device_os_version", length = 20)
    private String deviceOsVersion;
    
    @Enumerated(EnumType.STRING)
    private KeyType keyType;
    
    @Enumerated(EnumType.STRING)
    private KeyStatus status;
    
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(name = "ui_bundle")
    private String uiBundle;
    
    @Column(name = "vehicle_mobilization_data")
    private String vehicleMobilizationData;
    
    @Email(message = "Invalid friend email format")
    @Column(name = "friend_email", length = 100)
    private String friendEmail;

    @Column(name = "friend_name", length = 100)
    private String friendName;

    @Column(name = "friend_phone", length = 20)
    private String friendPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level")
    private PermissionLevel permissionLevel = PermissionLevel.FULL_ACCESS;

    @Column(name = "usage_count")
    private Long usageCount = 0L;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by", length = 100)
    private String revokedBy;

    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;

    @Column(name = "max_usage_count")
    private Long maxUsageCount;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "time_restrictions")
    private String timeRestrictions; // JSON format for time-based restrictions

    @Column(name = "location_restrictions")
    private String locationRestrictions; // JSON format for geo-fencing
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    // Constructors
    public DigitalKey() {}
    
    public DigitalKey(String keyId, String deviceId, String deviceOem, KeyType keyType, Vehicle vehicle) {
        this.keyId = keyId;
        this.deviceId = deviceId;
        this.deviceOem = deviceOem;
        this.keyType = keyType;
        this.vehicle = vehicle;
        this.status = KeyStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public KeyType getKeyType() { return keyType; }
    public void setKeyType(KeyType keyType) { this.keyType = keyType; }
    
    public KeyStatus getStatus() { return status; }
    public void setStatus(KeyStatus status) { this.status = status; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getUiBundle() { return uiBundle; }
    public void setUiBundle(String uiBundle) { this.uiBundle = uiBundle; }
    
    public String getVehicleMobilizationData() { return vehicleMobilizationData; }
    public void setVehicleMobilizationData(String vehicleMobilizationData) { this.vehicleMobilizationData = vehicleMobilizationData; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    // New getters and setters for business fields
    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getDeviceOs() { return deviceOs; }
    public void setDeviceOs(String deviceOs) { this.deviceOs = deviceOs; }

    public String getDeviceOsVersion() { return deviceOsVersion; }
    public void setDeviceOsVersion(String deviceOsVersion) { this.deviceOsVersion = deviceOsVersion; }

    public String getFriendName() { return friendName; }
    public void setFriendName(String friendName) { this.friendName = friendName; }

    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }

    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }

    public Long getUsageCount() { return usageCount; }
    public void setUsageCount(Long usageCount) { this.usageCount = usageCount; }

    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }

    public String getRevocationReason() { return revocationReason; }
    public void setRevocationReason(String revocationReason) { this.revocationReason = revocationReason; }

    public Long getMaxUsageCount() { return maxUsageCount; }
    public void setMaxUsageCount(Long maxUsageCount) { this.maxUsageCount = maxUsageCount; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public String getTimeRestrictions() { return timeRestrictions; }
    public void setTimeRestrictions(String timeRestrictions) { this.timeRestrictions = timeRestrictions; }

    public String getLocationRestrictions() { return locationRestrictions; }
    public void setLocationRestrictions(String locationRestrictions) { this.locationRestrictions = locationRestrictions; }

    // Business methods
    public boolean isActive() {
        return status == KeyStatus.ACTIVE && !isExpired() && !isRevoked() && isWithinValidPeriod() && !isUsageLimitReached();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isWithinValidPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return (validFrom == null || validFrom.isBefore(now)) &&
               (expiresAt == null || expiresAt.isAfter(now));
    }

    public boolean isUsageLimitReached() {
        return maxUsageCount != null && usageCount >= maxUsageCount;
    }

    public boolean isFriendKey() {
        return keyType == KeyType.FRIEND;
    }

    public void activate() {
        this.status = KeyStatus.ACTIVE;
        this.activatedAt = LocalDateTime.now();
        if (this.validFrom == null) {
            this.validFrom = LocalDateTime.now();
        }
    }

    public void revoke(String revokedBy, String reason) {
        this.status = KeyStatus.TERMINATED;
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = revokedBy;
        this.revocationReason = reason;
    }

    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();

        // Auto-revoke if usage limit reached
        if (isUsageLimitReached()) {
            this.status = KeyStatus.EXPIRED;
        }
    }

    public long getRemainingUsages() {
        if (maxUsageCount == null) {
            return Long.MAX_VALUE;
        }
        return Math.max(0, maxUsageCount - usageCount);
    }

    public boolean canBeUsedAt(LocalDateTime dateTime) {
        return (validFrom == null || !validFrom.isAfter(dateTime)) &&
               (expiresAt == null || !expiresAt.isBefore(dateTime));
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
