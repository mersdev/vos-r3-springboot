package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class CompletePairingRequest {
    @NotBlank
    @JsonProperty("sessionId")
    private String sessionId;
    
    @NotBlank
    @JsonProperty("pairingPassword")
    private String pairingPassword;
    
    @NotBlank
    @JsonProperty("deviceId")
    private String deviceId;
    
    @NotBlank
    @JsonProperty("deviceOem")
    private String deviceOem;
    
    @JsonProperty("devicePublicKey")
    private String devicePublicKey;
    
    @JsonProperty("deviceCertificate")
    private String deviceCertificate;
    
    // Constructors
    public CompletePairingRequest() {}
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getPairingPassword() { return pairingPassword; }
    public void setPairingPassword(String pairingPassword) { this.pairingPassword = pairingPassword; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public String getDevicePublicKey() { return devicePublicKey; }
    public void setDevicePublicKey(String devicePublicKey) { this.devicePublicKey = devicePublicKey; }
    
    public String getDeviceCertificate() { return deviceCertificate; }
    public void setDeviceCertificate(String deviceCertificate) { this.deviceCertificate = deviceCertificate; }
}
