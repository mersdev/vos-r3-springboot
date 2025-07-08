package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response from digital key management operation")
public class ManageKeyResponse {
    @JsonProperty("success")
    @Schema(description = "Indicates if the key management operation was successful",
            example = "true")
    private boolean success;

    @JsonProperty("message")
    @Schema(description = "Human-readable message describing the operation result",
            example = "Digital key suspended successfully")
    private String message;

    @JsonProperty("keyId")
    @Schema(description = "The unique identifier of the managed key",
            example = "OWNER-KEY-12345678")
    private String keyId;

    @JsonProperty("newStatus")
    @Schema(description = "The new status of the digital key after the operation",
            example = "SUSPENDED",
            allowableValues = {"ACTIVE", "SUSPENDED", "TERMINATED", "EXPIRED"})
    private String newStatus;
    
    public ManageKeyResponse() {}
    
    public ManageKeyResponse(boolean success, String message, String keyId, String newStatus) {
        this.success = success;
        this.message = message;
        this.keyId = keyId;
        this.newStatus = newStatus;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
