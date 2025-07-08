package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelematicsResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("commandId")
    private String commandId;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
}
