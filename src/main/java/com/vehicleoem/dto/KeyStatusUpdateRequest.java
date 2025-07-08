package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyStatusUpdateRequest {
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("newStatus")
    private String newStatus;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
