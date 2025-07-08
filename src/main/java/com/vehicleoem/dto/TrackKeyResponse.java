package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response from digital key tracking operation")
public class TrackKeyResponse {
    @JsonProperty("success")
    @Schema(description = "Indicates if the key tracking operation was successful",
            example = "true")
    private boolean success;

    @JsonProperty("message")
    @Schema(description = "Human-readable message describing the operation result",
            example = "Digital key tracked successfully")
    private String message;

    @JsonProperty("keyId")
    @Schema(description = "The unique identifier of the tracked key",
            example = "OWNER-KEY-12345678")
    private String keyId;

    @JsonProperty("trackingId")
    @Schema(description = "Unique tracking identifier for this operation",
            example = "TRK-2025-001234")
    private String trackingId;
    
    public TrackKeyResponse() {}
    
    public TrackKeyResponse(boolean success, String message, String keyId, String trackingId) {
        this.success = success;
        this.message = message;
        this.keyId = keyId;
        this.trackingId = trackingId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
}
