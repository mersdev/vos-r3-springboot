package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true, length = 17)
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters, excluding I, O, Q")
    private String vin;

    @NotBlank
    @Size(min = 1, max = 50, message = "Make must be between 1 and 50 characters")
    @Column(length = 50)
    private String make;

    @NotBlank
    @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
    @Column(length = 50)
    private String model;

    @NotNull
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2030, message = "Year cannot be more than 2030")
    @Column(name = "model_year")
    private Integer year;

    @Column(length = 30)
    private String color;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Column(name = "engine_type", length = 50)
    private String engineType;

    @Column(name = "transmission_type", length = 30)
    private String transmissionType;

    @Column(name = "fuel_type", length = 30)
    private String fuelType;

    @Column(name = "mileage")
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_status")
    private VehicleStatus vehicleStatus = VehicleStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_tier")
    private SubscriptionTier subscriptionTier = SubscriptionTier.BASIC;
    
    @Column(name = "public_key_certificate", columnDefinition = "TEXT")
    private String publicKeyCertificate;
    
    @Column(name = "pairing_password")
    private String pairingPassword;
    
    @Column(name = "pairing_verifier")
    private String pairingVerifier;
    
    @Column(name = "subscription_active")
    private Boolean subscriptionActive = true;
    
    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @Column(name = "max_keys_allowed")
    private Integer maxKeysAllowed = 5;

    @Column(name = "current_key_count")
    private Integer currentKeyCount = 0;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "total_key_usage_count")
    private Long totalKeyUsageCount = 0L;

    @Column(name = "warranty_expires_at")
    private LocalDateTime warrantyExpiresAt;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "dealer_name", length = 100)
    private String dealerName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private OwnerAccount owner;
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DigitalKey> digitalKeys;
    
    // Constructors
    public Vehicle() {}
    
    public Vehicle(String vin, String make, String model, Integer year, OwnerAccount owner) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getPublicKeyCertificate() { return publicKeyCertificate; }
    public void setPublicKeyCertificate(String publicKeyCertificate) { this.publicKeyCertificate = publicKeyCertificate; }
    
    public String getPairingPassword() { return pairingPassword; }
    public void setPairingPassword(String pairingPassword) { this.pairingPassword = pairingPassword; }
    
    public String getPairingVerifier() { return pairingVerifier; }
    public void setPairingVerifier(String pairingVerifier) { this.pairingVerifier = pairingVerifier; }
    
    public Boolean getSubscriptionActive() { return subscriptionActive; }
    public void setSubscriptionActive(Boolean subscriptionActive) { this.subscriptionActive = subscriptionActive; }
    
    public LocalDateTime getSubscriptionExpiresAt() { return subscriptionExpiresAt; }
    public void setSubscriptionExpiresAt(LocalDateTime subscriptionExpiresAt) { this.subscriptionExpiresAt = subscriptionExpiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public OwnerAccount getOwner() { return owner; }
    public void setOwner(OwnerAccount owner) { this.owner = owner; }
    
    public List<DigitalKey> getDigitalKeys() { return digitalKeys; }
    public void setDigitalKeys(List<DigitalKey> digitalKeys) { this.digitalKeys = digitalKeys; }
    
    // New getters and setters for business fields
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }

    public String getTransmissionType() { return transmissionType; }
    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
    public void setVehicleStatus(VehicleStatus vehicleStatus) { this.vehicleStatus = vehicleStatus; }

    public SubscriptionTier getSubscriptionTier() { return subscriptionTier; }
    public void setSubscriptionTier(SubscriptionTier subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
        this.maxKeysAllowed = subscriptionTier.getMaxKeys();
    }

    public Integer getMaxKeysAllowed() { return maxKeysAllowed; }
    public void setMaxKeysAllowed(Integer maxKeysAllowed) { this.maxKeysAllowed = maxKeysAllowed; }

    public Integer getCurrentKeyCount() { return currentKeyCount; }
    public void setCurrentKeyCount(Integer currentKeyCount) { this.currentKeyCount = currentKeyCount; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public Long getTotalKeyUsageCount() { return totalKeyUsageCount; }
    public void setTotalKeyUsageCount(Long totalKeyUsageCount) { this.totalKeyUsageCount = totalKeyUsageCount; }

    public LocalDateTime getWarrantyExpiresAt() { return warrantyExpiresAt; }
    public void setWarrantyExpiresAt(LocalDateTime warrantyExpiresAt) { this.warrantyExpiresAt = warrantyExpiresAt; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    // Business methods
    public boolean canAddMoreKeys() {
        return currentKeyCount < maxKeysAllowed;
    }

    public boolean isSubscriptionValid() {
        return subscriptionActive &&
               (subscriptionExpiresAt == null || subscriptionExpiresAt.isAfter(LocalDateTime.now()));
    }

    public boolean isWarrantyValid() {
        return warrantyExpiresAt != null && warrantyExpiresAt.isAfter(LocalDateTime.now());
    }

    public boolean canCreateFriendKeys() {
        return subscriptionTier.isFriendKeysAllowed() && isSubscriptionValid();
    }

    public void incrementKeyCount() {
        this.currentKeyCount++;
    }

    public void decrementKeyCount() {
        if (this.currentKeyCount > 0) {
            this.currentKeyCount--;
        }
    }

    public void incrementKeyUsage() {
        this.totalKeyUsageCount++;
        this.lastActivityAt = LocalDateTime.now();
    }

    public boolean isVinValid() {
        return vin != null && vin.matches("^[A-HJ-NPR-Z0-9]{17}$");
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
