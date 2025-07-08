package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyValidationRequest {
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
