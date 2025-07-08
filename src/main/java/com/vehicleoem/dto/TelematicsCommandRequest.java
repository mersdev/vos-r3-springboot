package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelematicsCommandRequest {
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("command")
    private String command;
    
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
