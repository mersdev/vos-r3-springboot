package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "pairing_sessions")
public class PairingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @NotBlank
    @Column(name = "session_id", unique = true, length = 50)
    private String sessionId;
    
    @NotBlank
    @Column(name = "pairing_password", length = 100)
    private String pairingPassword;
    
    @NotBlank
    @Column(name = "pairing_verifier", length = 255)
    private String pairingVerifier;
    
    @Column(name = "vehicle_public_key", columnDefinition = "TEXT")
    private String vehiclePublicKey;

    @Column(name = "vehicle_private_key", columnDefinition = "TEXT")
    private String vehiclePrivateKey;
    
    @Column(name = "device_id", length = 100)
    private String deviceId;
    
    @Column(name = "device_oem", length = 50)
    private String deviceOem;
    
    @Column(name = "device_public_key", columnDefinition = "TEXT")
    private String devicePublicKey;

    @Column(name = "device_certificate", columnDefinition = "TEXT")
    private String deviceCertificate;

    @Column(name = "cross_signed_certificate", columnDefinition = "TEXT")
    private String crossSignedCertificate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PairingStatus status = PairingStatus.INITIATED;
    
    @NotNull
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "initiated_by", length = 100)
    private String initiatedBy;
    
    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "revoked_by", length = 100)
    private String revokedBy;
    
    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public PairingSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.initiatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getPairingPassword() { return pairingPassword; }
    public void setPairingPassword(String pairingPassword) { this.pairingPassword = pairingPassword; }
    
    public String getPairingVerifier() { return pairingVerifier; }
    public void setPairingVerifier(String pairingVerifier) { this.pairingVerifier = pairingVerifier; }
    
    public String getVehiclePublicKey() { return vehiclePublicKey; }
    public void setVehiclePublicKey(String vehiclePublicKey) { this.vehiclePublicKey = vehiclePublicKey; }
    
    public String getVehiclePrivateKey() { return vehiclePrivateKey; }
    public void setVehiclePrivateKey(String vehiclePrivateKey) { this.vehiclePrivateKey = vehiclePrivateKey; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public String getDevicePublicKey() { return devicePublicKey; }
    public void setDevicePublicKey(String devicePublicKey) { this.devicePublicKey = devicePublicKey; }
    
    public String getDeviceCertificate() { return deviceCertificate; }
    public void setDeviceCertificate(String deviceCertificate) { this.deviceCertificate = deviceCertificate; }
    
    public String getCrossSignedCertificate() { return crossSignedCertificate; }
    public void setCrossSignedCertificate(String crossSignedCertificate) { this.crossSignedCertificate = crossSignedCertificate; }
    
    public PairingStatus getStatus() { return status; }
    public void setStatus(PairingStatus status) { this.status = status; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }
    
    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Integer getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }
    
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
    
    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }
    
    public String getRevocationReason() { return revocationReason; }
    public void setRevocationReason(String revocationReason) { this.revocationReason = revocationReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
    
    public boolean isActive() {
        return status == PairingStatus.INITIATED && !isExpired() && failedAttempts < 3;
    }
    
    public void incrementFailedAttempts() {
        this.failedAttempts++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean canRetry() {
        return failedAttempts < 3 && !isExpired() && status == PairingStatus.INITIATED;
    }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
