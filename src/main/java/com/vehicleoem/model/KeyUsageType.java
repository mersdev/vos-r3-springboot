package com.vehicleoem.model;

public enum KeyUsageType {
    UNLOCK("Vehicle unlocked"),
    LOCK("Vehicle locked"),
    START_ENGINE("Engine started"),
    STOP_ENGINE("Engine stopped"),
    TRUNK_ACCESS("Trunk accessed"),
    PANIC_BUTTON("Panic button pressed"),
    REMOTE_START("Remote engine start"),
    CLIMATE_CONTROL("Climate control activated"),
    HORN_LIGHTS("Horn and lights activated"),
    VALET_MODE("Valet mode activated"),
    EMERGENCY_ACCESS("Emergency access used");
    
    private final String description;
    
    KeyUsageType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
