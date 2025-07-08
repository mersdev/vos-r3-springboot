package com.vehicleoem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vehicle Model Tests")
class VehicleTest {

    private Validator validator;
    private Vehicle vehicle;
    private OwnerAccount owner;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        owner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        vehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, owner);
    }

    @Test
    @DisplayName("Should create valid Vehicle")
    void shouldCreateValidVehicle() {
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(vehicle);
        assertTrue(violations.isEmpty());
        
        assertEquals("1HGBH41JXMN109186", vehicle.getVin());
        assertEquals("Honda", vehicle.getMake());
        assertEquals("Civic", vehicle.getModel());
        assertEquals(2023, vehicle.getYear());
        assertEquals(owner, vehicle.getOwner());
        assertEquals(VehicleStatus.ACTIVE, vehicle.getVehicleStatus());
        assertEquals(SubscriptionTier.BASIC, vehicle.getSubscriptionTier());
        assertTrue(vehicle.getSubscriptionActive());
        assertEquals(5, vehicle.getMaxKeysAllowed());
        assertEquals(0, vehicle.getCurrentKeyCount());
        assertEquals(0L, vehicle.getTotalKeyUsageCount());
        assertNotNull(vehicle.getCreatedAt());
        assertNotNull(vehicle.getUpdatedAt());
    }

    @Test
    @DisplayName("Should validate VIN format")
    void shouldValidateVinFormat() {
        // Test invalid VIN with I, O, Q
        vehicle.setVin("1HGBH41JXIN109186"); // Contains 'I'
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(vehicle);
        assertFalse(violations.isEmpty());
        
        // Test invalid VIN length
        vehicle.setVin("1HGBH41JX"); // Too short
        violations = validator.validate(vehicle);
        assertFalse(violations.isEmpty());
        
        // Test valid VIN
        vehicle.setVin("1HGBH41JXMN109186");
        violations = validator.validate(vehicle);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate year constraints")
    void shouldValidateYearConstraints() {
        // Test year too old
        vehicle.setYear(1899);
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(vehicle);
        assertFalse(violations.isEmpty());
        
        // Test year too far in future
        vehicle.setYear(2031);
        violations = validator.validate(vehicle);
        assertFalse(violations.isEmpty());
        
        // Test valid year
        vehicle.setYear(2023);
        violations = validator.validate(vehicle);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle subscription tier changes")
    void shouldHandleSubscriptionTierChanges() {
        assertEquals(SubscriptionTier.BASIC, vehicle.getSubscriptionTier());
        assertEquals(5, vehicle.getMaxKeysAllowed());
        
        // Upgrade to Premium
        vehicle.setSubscriptionTier(SubscriptionTier.PREMIUM);
        assertEquals(SubscriptionTier.PREMIUM, vehicle.getSubscriptionTier());
        assertEquals(20, vehicle.getMaxKeysAllowed());
        
        // Upgrade to Enterprise
        vehicle.setSubscriptionTier(SubscriptionTier.ENTERPRISE);
        assertEquals(SubscriptionTier.ENTERPRISE, vehicle.getSubscriptionTier());
        assertEquals(100, vehicle.getMaxKeysAllowed());
    }

    @Test
    @DisplayName("Should check key capacity")
    void shouldCheckKeyCapacity() {
        assertTrue(vehicle.canAddMoreKeys());
        
        // Set current key count to max
        vehicle.setCurrentKeyCount(5);
        assertFalse(vehicle.canAddMoreKeys());
        
        // Upgrade subscription
        vehicle.setSubscriptionTier(SubscriptionTier.PREMIUM);
        assertTrue(vehicle.canAddMoreKeys());
    }

    @Test
    @DisplayName("Should validate subscription status")
    void shouldValidateSubscriptionStatus() {
        assertTrue(vehicle.isSubscriptionValid());
        
        // Deactivate subscription
        vehicle.setSubscriptionActive(false);
        assertFalse(vehicle.isSubscriptionValid());
        
        // Reactivate but set expiration in past
        vehicle.setSubscriptionActive(true);
        vehicle.setSubscriptionExpiresAt(LocalDateTime.now().minusDays(1));
        assertFalse(vehicle.isSubscriptionValid());
        
        // Set expiration in future
        vehicle.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(30));
        assertTrue(vehicle.isSubscriptionValid());
    }

    @Test
    @DisplayName("Should check friend key permissions")
    void shouldCheckFriendKeyPermissions() {
        // Basic tier doesn't allow friend keys
        assertFalse(vehicle.canCreateFriendKeys());
        
        // Premium tier allows friend keys
        vehicle.setSubscriptionTier(SubscriptionTier.PREMIUM);
        assertTrue(vehicle.canCreateFriendKeys());
        
        // But not if subscription is expired
        vehicle.setSubscriptionActive(false);
        assertFalse(vehicle.canCreateFriendKeys());
    }

    @Test
    @DisplayName("Should handle key count operations")
    void shouldHandleKeyCountOperations() {
        assertEquals(0, vehicle.getCurrentKeyCount());
        
        vehicle.incrementKeyCount();
        assertEquals(1, vehicle.getCurrentKeyCount());
        
        vehicle.incrementKeyCount();
        assertEquals(2, vehicle.getCurrentKeyCount());
        
        vehicle.decrementKeyCount();
        assertEquals(1, vehicle.getCurrentKeyCount());
        
        vehicle.decrementKeyCount();
        assertEquals(0, vehicle.getCurrentKeyCount());
        
        // Should not go below 0
        vehicle.decrementKeyCount();
        assertEquals(0, vehicle.getCurrentKeyCount());
    }

    @Test
    @DisplayName("Should handle usage tracking")
    void shouldHandleUsageTracking() {
        assertEquals(0L, vehicle.getTotalKeyUsageCount());
        assertNull(vehicle.getLastActivityAt());
        
        vehicle.incrementKeyUsage();
        
        assertEquals(1L, vehicle.getTotalKeyUsageCount());
        assertNotNull(vehicle.getLastActivityAt());
        assertTrue(vehicle.getLastActivityAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Should validate VIN check digit")
    void shouldValidateVinCheckDigit() {
        assertTrue(vehicle.isVinValid());
        
        vehicle.setVin("INVALID_VIN_FORMAT");
        assertFalse(vehicle.isVinValid());
        
        vehicle.setVin("1HGBH41JXMN109186");
        assertTrue(vehicle.isVinValid());
    }

    @Test
    @DisplayName("Should handle warranty information")
    void shouldHandleWarrantyInformation() {
        assertNull(vehicle.getWarrantyExpiresAt());
        assertFalse(vehicle.isWarrantyValid());
        
        // Set warranty expiration in future
        vehicle.setWarrantyExpiresAt(LocalDateTime.now().plusYears(3));
        assertTrue(vehicle.isWarrantyValid());
        
        // Set warranty expiration in past
        vehicle.setWarrantyExpiresAt(LocalDateTime.now().minusDays(1));
        assertFalse(vehicle.isWarrantyValid());
    }

    @Test
    @DisplayName("Should handle vehicle details")
    void shouldHandleVehicleDetails() {
        vehicle.setColor("Blue");
        vehicle.setLicensePlate("ABC123");
        vehicle.setEngineType("V6");
        vehicle.setTransmissionType("Automatic");
        vehicle.setFuelType("Gasoline");
        vehicle.setMileage(15000);
        vehicle.setPurchaseDate(LocalDateTime.now().minusYears(1));
        vehicle.setDealerName("Honda of Downtown");
        
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(vehicle);
        assertTrue(violations.isEmpty());
        
        assertEquals("Blue", vehicle.getColor());
        assertEquals("ABC123", vehicle.getLicensePlate());
        assertEquals("V6", vehicle.getEngineType());
        assertEquals("Automatic", vehicle.getTransmissionType());
        assertEquals("Gasoline", vehicle.getFuelType());
        assertEquals(15000, vehicle.getMileage());
        assertEquals("Honda of Downtown", vehicle.getDealerName());
        assertNotNull(vehicle.getPurchaseDate());
    }

    @Test
    @DisplayName("Should update timestamps on modification")
    void shouldUpdateTimestampsOnModification() {
        LocalDateTime originalUpdatedAt = vehicle.getUpdatedAt();
        
        // Simulate update
        vehicle.preUpdate();
        
        assertTrue(vehicle.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should handle pairing credentials")
    void shouldHandlePairingCredentials() {
        assertNull(vehicle.getPairingPassword());
        assertNull(vehicle.getPairingVerifier());
        
        vehicle.setPairingPassword("test-password");
        vehicle.setPairingVerifier("test-verifier");
        
        assertEquals("test-password", vehicle.getPairingPassword());
        assertEquals("test-verifier", vehicle.getPairingVerifier());
    }
}
