package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vehicleoem.model.PermissionLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

public class ShareKeyRequest {
    @NotBlank
    @JsonProperty("vehicleVin")
    private String vehicleVin;
    
    @NotBlank
    @Email
    @JsonProperty("friendEmail")
    private String friendEmail;
    
    @JsonProperty("friendName")
    private String friendName;
    
    @JsonProperty("friendPhone")
    private String friendPhone;
    
    @JsonProperty("permissionLevel")
    private PermissionLevel permissionLevel = PermissionLevel.DRIVE_ONLY;
    
    @JsonProperty("expiresAt")
    private LocalDateTime expiresAt;
    
    @JsonProperty("timeRestrictions")
    private String timeRestrictions;
    
    @JsonProperty("locationRestrictions")
    private String locationRestrictions;
    
    @JsonProperty("maxUsageCount")
    private Long maxUsageCount;
    
    @NotBlank
    @JsonProperty("sharedBy")
    private String sharedBy;
    
    @JsonProperty("message")
    private String message;
    
    // Constructors
    public ShareKeyRequest() {}
    
    // Getters and Setters
    public String getVehicleVin() { return vehicleVin; }
    public void setVehicleVin(String vehicleVin) { this.vehicleVin = vehicleVin; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public String getFriendName() { return friendName; }
    public void setFriendName(String friendName) { this.friendName = friendName; }
    
    public String getFriendPhone() { return friendPhone; }
    public void setFriendPhone(String friendPhone) { this.friendPhone = friendPhone; }
    
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getTimeRestrictions() { return timeRestrictions; }
    public void setTimeRestrictions(String timeRestrictions) { this.timeRestrictions = timeRestrictions; }
    
    public String getLocationRestrictions() { return locationRestrictions; }
    public void setLocationRestrictions(String locationRestrictions) { this.locationRestrictions = locationRestrictions; }
    
    public Long getMaxUsageCount() { return maxUsageCount; }
    public void setMaxUsageCount(Long maxUsageCount) { this.maxUsageCount = maxUsageCount; }
    
    public String getSharedBy() { return sharedBy; }
    public void setSharedBy(String sharedBy) { this.sharedBy = sharedBy; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
