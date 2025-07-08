package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InitiatePairingResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("pairingPassword")
    private String pairingPassword;
    
    @JsonProperty("vehiclePublicKey")
    private String vehiclePublicKey;
    
    // Constructors
    public InitiatePairingResponse() {}
    
    public InitiatePairingResponse(boolean success, String message, String sessionId, 
                                 String pairingPassword, String vehiclePublicKey) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
        this.pairingPassword = pairingPassword;
        this.vehiclePublicKey = vehiclePublicKey;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getPairingPassword() { return pairingPassword; }
    public void setPairingPassword(String pairingPassword) { this.pairingPassword = pairingPassword; }
    
    public String getVehiclePublicKey() { return vehiclePublicKey; }
    public void setVehiclePublicKey(String vehiclePublicKey) { this.vehiclePublicKey = vehiclePublicKey; }
}
