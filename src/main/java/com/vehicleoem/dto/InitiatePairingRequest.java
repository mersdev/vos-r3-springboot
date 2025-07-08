package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class InitiatePairingRequest {
    @NotBlank
    @JsonProperty("vin")
    private String vin;
    
    @NotBlank
    @JsonProperty("initiatedBy")
    private String initiatedBy;
    
    @JsonProperty("reason")
    private String reason;
    
    // Constructors
    public InitiatePairingRequest() {}
    
    // Getters and Setters
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
