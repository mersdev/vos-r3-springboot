package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.dto.TrackKeyRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    
    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{9,14}$");
    
    public void validateVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) {
            throw new IllegalArgumentException("VIN cannot be null or empty");
        }
        
        if (vin.length() != 17) {
            throw new IllegalArgumentException("VIN must be exactly 17 characters");
        }
        
        if (!VIN_PATTERN.matcher(vin).matches()) {
            throw new IllegalArgumentException("VIN contains invalid characters. Cannot contain I, O, or Q");
        }
        
        // VIN check digit validation (simplified)
        if (!isValidVinCheckDigit(vin)) {
            throw new IllegalArgumentException("Invalid VIN check digit");
        }
    }
    
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
    }
    
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }
    }
    
    public void validateTrackKeyRequest(TrackKeyRequest request, Vehicle vehicle) {
        // Basic field validation
        if (request.getKeyId() == null || request.getKeyId().trim().isEmpty()) {
            throw new IllegalArgumentException("Key ID is required");
        }
        
        if (request.getKeyId().length() < 10 || request.getKeyId().length() > 100) {
            throw new IllegalArgumentException("Key ID must be between 10 and 100 characters");
        }
        
        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID is required");
        }
        
        if (request.getVehicleId() == null || request.getVehicleId().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        
        validateVin(request.getVehicleId());
        
        // Key type validation
        try {
            KeyType.valueOf(request.getKeyType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key type. Must be OWNER or FRIEND");
        }
        
        // Friend key specific validation
        if (KeyType.FRIEND.name().equalsIgnoreCase(request.getKeyType())) {
            if (!vehicle.canCreateFriendKeys()) {
                throw new IllegalArgumentException("Friend keys are not allowed for this subscription tier");
            }
            
            if (request.getFriendEmail() == null || request.getFriendEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Friend email is required for friend keys");
            }
            
            validateEmail(request.getFriendEmail());
        }
        
        // Expiration date validation
        if (request.getExpiresAt() != null && !request.getExpiresAt().trim().isEmpty()) {
            try {
                LocalDateTime expirationDate = LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ISO_DATE_TIME);
                
                if (expirationDate.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Expiration date cannot be in the past");
                }
                
                // Check against subscription tier limits
                LocalDateTime maxAllowedExpiration = LocalDateTime.now().plusDays(vehicle.getSubscriptionTier().getKeyExpirationDays());
                if (expirationDate.isAfter(maxAllowedExpiration)) {
                    throw new IllegalArgumentException("Expiration date exceeds subscription tier limit of " + 
                        vehicle.getSubscriptionTier().getKeyExpirationDays() + " days");
                }
                
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid expiration date format. Use ISO-8601 format (yyyy-MM-ddTHH:mm:ss)");
            }
        }
    }
    
    public void validateVehicleSubscription(Vehicle vehicle) {
        if (!vehicle.getSubscriptionActive()) {
            throw new IllegalArgumentException("Vehicle subscription is not active");
        }
        
        if (vehicle.getSubscriptionExpiresAt() != null && 
            vehicle.getSubscriptionExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Vehicle subscription has expired");
        }
        
        if (vehicle.getVehicleStatus() != VehicleStatus.ACTIVE) {
            throw new IllegalArgumentException("Vehicle is not in active status: " + vehicle.getVehicleStatus());
        }
    }
    
    public void validateKeyLimits(Vehicle vehicle) {
        if (!vehicle.canAddMoreKeys()) {
            throw new IllegalArgumentException("Maximum number of keys reached for this vehicle (" + 
                vehicle.getMaxKeysAllowed() + ")");
        }
    }
    
    public void validateOwnerAccount(OwnerAccount owner) {
        if (!owner.isAccountActive()) {
            throw new IllegalArgumentException("Owner account is not active: " + owner.getAccountStatus());
        }
        
        if (owner.isAccountLocked()) {
            throw new IllegalArgumentException("Owner account is locked until: " + owner.getAccountLockedUntil());
        }
        
        if (!owner.getEmailVerified()) {
            throw new IllegalArgumentException("Owner email must be verified before creating keys");
        }
    }
    
    public void validateKeyAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action is required");
        }
        
        String upperAction = action.toUpperCase();
        if (!upperAction.equals("SUSPEND") && !upperAction.equals("RESUME") && 
            !upperAction.equals("TERMINATE") && !upperAction.equals("EXPIRE")) {
            throw new IllegalArgumentException("Invalid action. Must be SUSPEND, RESUME, TERMINATE, or EXPIRE");
        }
    }
    
    public void validateKeyStatusTransition(DigitalKey key, String action) {
        KeyStatus currentStatus = key.getStatus();
        String upperAction = action.toUpperCase();
        
        switch (upperAction) {
            case "SUSPEND":
                if (currentStatus != KeyStatus.ACTIVE) {
                    throw new IllegalArgumentException("Can only suspend active keys");
                }
                break;
            case "RESUME":
                if (currentStatus != KeyStatus.SUSPENDED) {
                    throw new IllegalArgumentException("Can only resume suspended keys");
                }
                if (key.isExpired()) {
                    throw new IllegalArgumentException("Cannot resume expired key");
                }
                break;
            case "TERMINATE":
                if (currentStatus == KeyStatus.TERMINATED) {
                    throw new IllegalArgumentException("Key is already terminated");
                }
                break;
            case "EXPIRE":
                if (currentStatus == KeyStatus.EXPIRED || currentStatus == KeyStatus.TERMINATED) {
                    throw new IllegalArgumentException("Key is already expired or terminated");
                }
                break;
        }
    }
    
    private boolean isValidVinCheckDigit(String vin) {
        // Simplified VIN check digit validation
        // In a real implementation, this would use the full VIN algorithm
        char checkDigit = vin.charAt(8);
        return Character.isDigit(checkDigit) || checkDigit == 'X';
    }
    
    public void validateBusinessHours() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        // Business hours: Monday-Friday 6 AM - 10 PM, Saturday-Sunday 8 AM - 8 PM
        if (dayOfWeek >= 1 && dayOfWeek <= 5) { // Monday to Friday
            if (hour < 6 || hour >= 22) {
                throw new IllegalArgumentException("Key operations are only allowed during business hours (6 AM - 10 PM on weekdays)");
            }
        } else { // Weekend
            if (hour < 8 || hour >= 20) {
                throw new IllegalArgumentException("Key operations are only allowed during business hours (8 AM - 8 PM on weekends)");
            }
        }
    }
}
