package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyStatusUpdateResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
