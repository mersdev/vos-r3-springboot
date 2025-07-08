package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "key_usage_logs")
public class KeyUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "key_id", length = 100)
    private String keyId;
    
    @NotBlank
    @Column(name = "vehicle_vin", length = 17)
    private String vehicleVin;
    
    @NotBlank
    @Column(name = "device_id", length = 100)
    private String deviceId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type")
    private KeyUsageType usageType;
    
    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "location_latitude")
    private Double locationLatitude;
    
    @Column(name = "location_longitude")
    private Double locationLongitude;
    
    @Column(name = "location_address", length = 255)
    private String locationAddress;
    
    @Column(name = "success")
    private Boolean success = true;
    
    @Column(name = "error_message", length = 255)
    private String errorMessage;
    
    @Column(name = "session_duration_minutes")
    private Integer sessionDurationMinutes;
    
    @Column(name = "distance_traveled_km")
    private Double distanceTraveledKm;
    
    @Column(name = "max_speed_kmh")
    private Double maxSpeedKmh;
    
    @Column(name = "fuel_consumed_liters")
    private Double fuelConsumedLiters;
    
    @Column(name = "battery_level_start")
    private Integer batteryLevelStart;
    
    @Column(name = "battery_level_end")
    private Integer batteryLevelEnd;
    
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData; // JSON format for extensibility
    
    // Constructors
    public KeyUsageLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    public KeyUsageLog(String keyId, String vehicleVin, String deviceId, KeyUsageType usageType) {
        this();
        this.keyId = keyId;
        this.vehicleVin = vehicleVin;
        this.deviceId = deviceId;
        this.usageType = usageType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getVehicleVin() { return vehicleVin; }
    public void setVehicleVin(String vehicleVin) { this.vehicleVin = vehicleVin; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public KeyUsageType getUsageType() { return usageType; }
    public void setUsageType(KeyUsageType usageType) { this.usageType = usageType; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Double getLocationLatitude() { return locationLatitude; }
    public void setLocationLatitude(Double locationLatitude) { this.locationLatitude = locationLatitude; }
    
    public Double getLocationLongitude() { return locationLongitude; }
    public void setLocationLongitude(Double locationLongitude) { this.locationLongitude = locationLongitude; }
    
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
    
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getSessionDurationMinutes() { return sessionDurationMinutes; }
    public void setSessionDurationMinutes(Integer sessionDurationMinutes) { this.sessionDurationMinutes = sessionDurationMinutes; }
    
    public Double getDistanceTraveledKm() { return distanceTraveledKm; }
    public void setDistanceTraveledKm(Double distanceTraveledKm) { this.distanceTraveledKm = distanceTraveledKm; }
    
    public Double getMaxSpeedKmh() { return maxSpeedKmh; }
    public void setMaxSpeedKmh(Double maxSpeedKmh) { this.maxSpeedKmh = maxSpeedKmh; }
    
    public Double getFuelConsumedLiters() { return fuelConsumedLiters; }
    public void setFuelConsumedLiters(Double fuelConsumedLiters) { this.fuelConsumedLiters = fuelConsumedLiters; }
    
    public Integer getBatteryLevelStart() { return batteryLevelStart; }
    public void setBatteryLevelStart(Integer batteryLevelStart) { this.batteryLevelStart = batteryLevelStart; }
    
    public Integer getBatteryLevelEnd() { return batteryLevelEnd; }
    public void setBatteryLevelEnd(Integer batteryLevelEnd) { this.batteryLevelEnd = batteryLevelEnd; }
    
    public String getAdditionalData() { return additionalData; }
    public void setAdditionalData(String additionalData) { this.additionalData = additionalData; }
}
