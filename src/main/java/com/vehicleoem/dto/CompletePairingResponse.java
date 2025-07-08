package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletePairingResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("crossSignedCertificate")
    private String crossSignedCertificate;
    
    @JsonProperty("vehiclePublicKey")
    private String vehiclePublicKey;
    
    // Constructors
    public CompletePairingResponse() {}
    
    public CompletePairingResponse(boolean success, String message, 
                                 String crossSignedCertificate, String vehiclePublicKey) {
        this.success = success;
        this.message = message;
        this.crossSignedCertificate = crossSignedCertificate;
        this.vehiclePublicKey = vehiclePublicKey;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCrossSignedCertificate() { return crossSignedCertificate; }
    public void setCrossSignedCertificate(String crossSignedCertificate) { this.crossSignedCertificate = crossSignedCertificate; }
    
    public String getVehiclePublicKey() { return vehiclePublicKey; }
    public void setVehiclePublicKey(String vehiclePublicKey) { this.vehiclePublicKey = vehiclePublicKey; }
}
