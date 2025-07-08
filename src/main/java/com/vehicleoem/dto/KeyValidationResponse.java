package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyValidationResponse {
    @JsonProperty("valid")
    private boolean valid;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("keyStatus")
    private String keyStatus;
    
    // Getters and setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getKeyStatus() { return keyStatus; }
    public void setKeyStatus(String keyStatus) { this.keyStatus = keyStatus; }
}
