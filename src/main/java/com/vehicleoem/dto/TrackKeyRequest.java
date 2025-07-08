package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to track a new digital key for a vehicle")
public class TrackKeyRequest {
    @NotBlank
    @JsonProperty("keyId")
    @Schema(description = "Unique identifier for the digital key",
            example = "OWNER-KEY-12345678",
            minLength = 10, maxLength = 100,
            pattern = "^[A-Za-z0-9-_]{10,100}$")
    private String keyId;

    @NotBlank
    @JsonProperty("deviceId")
    @Schema(description = "Unique identifier of the device requesting the key",
            example = "IPHONE-14-ABC123",
            minLength = 5, maxLength = 100)
    private String deviceId;

    @NotBlank
    @JsonProperty("deviceOem")
    @Schema(description = "Original Equipment Manufacturer of the device",
            example = "Apple",
            allowableValues = {"Apple", "Samsung", "Google", "Huawei", "OnePlus"})
    private String deviceOem;

    @NotBlank
    @JsonProperty("vehicleId")
    @Schema(description = "Vehicle Identification Number (VIN)",
            example = "1HGBH41JXMN109186",
            pattern = "^[A-HJ-NPR-Z0-9]{17}$")
    private String vehicleId;

    @NotBlank
    @JsonProperty("keyType")
    @Schema(description = "Type of digital key being requested",
            example = "OWNER",
            allowableValues = {"OWNER", "FRIEND"})
    private String keyType;

    @JsonProperty("publicKey")
    @Schema(description = "Base64-encoded public key for cryptographic operations",
            example = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...")
    private String publicKey;

    @JsonProperty("uiBundle")
    @Schema(description = "UI bundle identifier for device-specific key presentation",
            example = "com.apple.carkey.ui.bundle")
    private String uiBundle;

    @JsonProperty("vehicleMobilizationData")
    @Schema(description = "Encrypted vehicle-specific mobilization data",
            example = "encrypted_mobilization_data_here")
    private String vehicleMobilizationData;

    @JsonProperty("friendEmail")
    @Schema(description = "Email address of friend (required for FRIEND key type)",
            example = "friend@example.com",
            format = "email")
    private String friendEmail;

    @JsonProperty("expiresAt")
    @Schema(description = "ISO-8601 formatted expiration date and time",
            example = "2025-12-31T23:59:59",
            format = "date-time")
    private String expiresAt;
    
    // Constructors
    public TrackKeyRequest() {}
    
    // Getters and Setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getUiBundle() { return uiBundle; }
    public void setUiBundle(String uiBundle) { this.uiBundle = uiBundle; }
    
    public String getVehicleMobilizationData() { return vehicleMobilizationData; }
    public void setVehicleMobilizationData(String vehicleMobilizationData) { this.vehicleMobilizationData = vehicleMobilizationData; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
