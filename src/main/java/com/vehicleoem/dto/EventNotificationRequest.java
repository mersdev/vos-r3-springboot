package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventNotificationRequest {
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("action")
    private String action;
    
    @JsonProperty("newStatus")
    private String newStatus;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
