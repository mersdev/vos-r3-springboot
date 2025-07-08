package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.repository.DigitalKeyRepository;
import com.vehicleoem.repository.VehicleRepository;
import com.vehicleoem.exception.KeyManagementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KeyUsageTrackingService {
    
    @Autowired
    private DigitalKeyRepository digitalKeyRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private ValidationService validationService;
    
    public void recordKeyUsage(String keyId, KeyUsageType usageType) {
        recordKeyUsage(keyId, usageType, null, null, null);
    }
    
    public void recordKeyUsage(String keyId, KeyUsageType usageType, Double latitude, Double longitude, String address) {
        // Find and validate the digital key
        DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
        
        // Validate key can be used
        validateKeyUsage(digitalKey);
        
        // Record the usage
        auditService.logKeyUsage(keyId, digitalKey.getVehicle().getVin(), digitalKey.getDeviceId(), usageType);
        
        // Record detailed usage if location provided
        if (latitude != null && longitude != null) {
            auditService.logDetailedKeyUsage(keyId, digitalKey.getVehicle().getVin(), digitalKey.getDeviceId(), 
                usageType, latitude, longitude, address, null, null, null);
        }
        
        // Update key usage statistics
        digitalKey.incrementUsage();
        digitalKeyRepository.save(digitalKey);
        
        // Update vehicle statistics
        Vehicle vehicle = digitalKey.getVehicle();
        vehicle.incrementKeyUsage();
        vehicleRepository.save(vehicle);
        
        // Log security events for suspicious usage patterns
        checkForSuspiciousActivity(digitalKey, usageType);
    }
    
    public void recordDetailedKeyUsage(String keyId, KeyUsageType usageType, 
                                     Double latitude, Double longitude, String address,
                                     Integer sessionDurationMinutes, Double distanceKm, Double maxSpeedKmh,
                                     Double fuelConsumed, Integer batteryStart, Integer batteryEnd) {
        
        // Find and validate the digital key
        DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
            .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
        
        // Validate key can be used
        validateKeyUsage(digitalKey);
        
        // Record detailed usage
        auditService.logDetailedKeyUsage(keyId, digitalKey.getVehicle().getVin(), digitalKey.getDeviceId(), 
            usageType, latitude, longitude, address, sessionDurationMinutes, distanceKm, maxSpeedKmh);
        
        // Update key usage statistics
        digitalKey.incrementUsage();
        digitalKeyRepository.save(digitalKey);
        
        // Update vehicle statistics
        Vehicle vehicle = digitalKey.getVehicle();
        vehicle.incrementKeyUsage();
        if (distanceKm != null && vehicle.getMileage() != null) {
            vehicle.setMileage((int) (vehicle.getMileage() + distanceKm));
        }
        vehicleRepository.save(vehicle);
        
        // Check for policy violations (speed, distance, etc.)
        checkForPolicyViolations(digitalKey, maxSpeedKmh, distanceKm, sessionDurationMinutes);
    }
    
    public void recordFailedKeyUsage(String keyId, KeyUsageType usageType, String errorMessage) {
        try {
            DigitalKey digitalKey = digitalKeyRepository.findByKeyId(keyId)
                .orElseThrow(() -> KeyManagementException.keyNotFound(keyId));
            
            // Log failed usage
            auditService.logKeyUsage(keyId, digitalKey.getVehicle().getVin(), digitalKey.getDeviceId(), 
                usageType, false, errorMessage);
            
            // Log security event for failed usage
            auditService.logSecurityEvent("DIGITAL_KEY", keyId, "FAILED_USAGE", "SYSTEM", 
                "Failed key usage: " + usageType + " - " + errorMessage);
                
        } catch (Exception e) {
            // Log the error but don't fail
            System.err.println("Failed to record failed key usage: " + e.getMessage());
        }
    }
    
    private void validateKeyUsage(DigitalKey digitalKey) {
        if (!digitalKey.isActive()) {
            throw KeyManagementException.invalidKeyStatus(digitalKey.getKeyId(), 
                digitalKey.getStatus().name(), "USE");
        }
        
        if (digitalKey.isExpired()) {
            throw KeyManagementException.keyExpired(digitalKey.getKeyId());
        }
        
        if (digitalKey.isUsageLimitReached()) {
            throw KeyManagementException.usageLimitReached(digitalKey.getKeyId());
        }
        
        // Validate vehicle subscription
        validationService.validateVehicleSubscription(digitalKey.getVehicle());
    }
    
    private void checkForSuspiciousActivity(DigitalKey digitalKey, KeyUsageType usageType) {
        try {
            // Check for unusual usage patterns
            Long recentUsageCount = auditService.getKeyUsageCount(digitalKey.getKeyId(), 1); // Last 24 hours
            
            // Alert if more than 50 uses in 24 hours
            if (recentUsageCount != null && recentUsageCount > 50) {
                auditService.logSecurityEvent("DIGITAL_KEY", digitalKey.getKeyId(), "SUSPICIOUS_USAGE", 
                    "SYSTEM", "Excessive key usage detected: " + recentUsageCount + " uses in 24 hours");
            }
            
            // Alert for emergency access usage
            if (usageType == KeyUsageType.EMERGENCY_ACCESS) {
                auditService.logSecurityEvent("DIGITAL_KEY", digitalKey.getKeyId(), "EMERGENCY_ACCESS", 
                    "SYSTEM", "Emergency access used");
            }
            
            // Alert for panic button usage
            if (usageType == KeyUsageType.PANIC_BUTTON) {
                auditService.logCriticalEvent("DIGITAL_KEY", digitalKey.getKeyId(), "PANIC_BUTTON", 
                    "SYSTEM", "Panic button activated");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to check for suspicious activity: " + e.getMessage());
        }
    }
    
    private void checkForPolicyViolations(DigitalKey digitalKey, Double maxSpeed, Double distance, Integer duration) {
        try {
            // Check speed violations for friend keys
            if (digitalKey.isFriendKey() && maxSpeed != null && maxSpeed > 120) { // 120 km/h limit for friend keys
                auditService.logSecurityEvent("DIGITAL_KEY", digitalKey.getKeyId(), "SPEED_VIOLATION", 
                    "SYSTEM", "Speed limit exceeded: " + maxSpeed + " km/h");
            }
            
            // Check distance violations for valet keys
            if (digitalKey.getPermissionLevel() == PermissionLevel.VALET && distance != null && distance > 50) { // 50km limit for valet
                auditService.logSecurityEvent("DIGITAL_KEY", digitalKey.getKeyId(), "DISTANCE_VIOLATION", 
                    "SYSTEM", "Distance limit exceeded: " + distance + " km");
            }
            
            // Check duration violations for temporary keys
            if (digitalKey.isFriendKey() && duration != null && duration > 480) { // 8 hours limit for friend keys
                auditService.logSecurityEvent("DIGITAL_KEY", digitalKey.getKeyId(), "DURATION_VIOLATION", 
                    "SYSTEM", "Usage duration exceeded: " + duration + " minutes");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to check for policy violations: " + e.getMessage());
        }
    }
}
