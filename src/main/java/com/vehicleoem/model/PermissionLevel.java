package com.vehicleoem.model;

public enum PermissionLevel {
    FULL_ACCESS("Full access to all vehicle functions"),
    DRIVE_ONLY("Can unlock and start vehicle"),
    UNLOCK_ONLY("Can only unlock vehicle"),
    TRUNK_ONLY("Can only access trunk"),
    EMERGENCY_ONLY("Emergency access only"),
    VALET("Valet mode with speed/distance restrictions");
    
    private final String description;
    
    PermissionLevel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
