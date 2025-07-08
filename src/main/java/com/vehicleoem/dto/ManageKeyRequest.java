package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to manage the lifecycle of an existing digital key")
public class ManageKeyRequest {
    @NotBlank
    @JsonProperty("keyId")
    @Schema(description = "Unique identifier of the digital key to manage",
            example = "OWNER-KEY-12345678",
            minLength = 10, maxLength = 100)
    private String keyId;

    @NotBlank
    @JsonProperty("action")
    @Schema(description = "Action to perform on the digital key",
            example = "SUSPEND",
            allowableValues = {"SUSPEND", "RESUME", "TERMINATE", "EXPIRE"})
    private String action;

    @JsonProperty("reason")
    @Schema(description = "Reason for performing this action",
            example = "Suspicious activity detected")
    private String reason;

    @JsonProperty("requestedBy")
    @Schema(description = "Identifier of the user or system requesting this action",
            example = "security-admin")
    private String requestedBy;
    
    public ManageKeyRequest() {}
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
}
