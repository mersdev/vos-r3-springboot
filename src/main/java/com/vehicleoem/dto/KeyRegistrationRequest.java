package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyRegistrationRequest {
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("keyType")
    private String keyType;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
