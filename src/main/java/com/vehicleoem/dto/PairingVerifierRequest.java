package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PairingVerifierRequest {
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("verifier")
    private String verifier;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVerifier() { return verifier; }
    public void setVerifier(String verifier) { this.verifier = verifier; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
