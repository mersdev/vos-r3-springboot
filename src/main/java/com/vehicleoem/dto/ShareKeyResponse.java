package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShareKeyResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("invitationCode")
    private String invitationCode;
    
    // Constructors
    public ShareKeyResponse() {}
    
    public ShareKeyResponse(boolean success, String message, String invitationCode) {
        this.success = success;
        this.message = message;
        this.invitationCode = invitationCode;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getInvitationCode() { return invitationCode; }
    public void setInvitationCode(String invitationCode) { this.invitationCode = invitationCode; }
}
