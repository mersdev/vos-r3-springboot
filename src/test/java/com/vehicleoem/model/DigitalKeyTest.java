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

@DisplayName("DigitalKey Model Tests")
class DigitalKeyTest {

    private Validator validator;
    private DigitalKey digitalKey;
    private Vehicle vehicle;
    private OwnerAccount owner;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        owner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        vehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, owner);
        digitalKey = new DigitalKey("OWNER-KEY-001", "DEVICE001", "Apple", KeyType.OWNER, vehicle);
    }

    @Test
    @DisplayName("Should create valid DigitalKey")
    void shouldCreateValidDigitalKey() {
        Set<ConstraintViolation<DigitalKey>> violations = validator.validate(digitalKey);
        assertTrue(violations.isEmpty());
        
        assertEquals("OWNER-KEY-001", digitalKey.getKeyId());
        assertEquals("DEVICE001", digitalKey.getDeviceId());
        assertEquals("Apple", digitalKey.getDeviceOem());
        assertEquals(KeyType.OWNER, digitalKey.getKeyType());
        assertEquals(vehicle, digitalKey.getVehicle());
        assertEquals(KeyStatus.ACTIVE, digitalKey.getStatus());
        assertEquals(PermissionLevel.FULL_ACCESS, digitalKey.getPermissionLevel());
        assertEquals(0L, digitalKey.getUsageCount());
        assertNotNull(digitalKey.getCreatedAt());
        assertNotNull(digitalKey.getUpdatedAt());
    }

    @Test
    @DisplayName("Should validate key ID format")
    void shouldValidateKeyIdFormat() {
        // Test invalid key ID
        digitalKey.setKeyId("invalid key id!");
        Set<ConstraintViolation<DigitalKey>> violations = validator.validate(digitalKey);
        assertFalse(violations.isEmpty());
        
        // Test valid key ID
        digitalKey.setKeyId("VALID-KEY_123456");
        violations = validator.validate(digitalKey);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate friend email format")
    void shouldValidateFriendEmailFormat() {
        digitalKey.setKeyType(KeyType.FRIEND);
        digitalKey.setFriendEmail("invalid-email");
        
        Set<ConstraintViolation<DigitalKey>> violations = validator.validate(digitalKey);
        assertFalse(violations.isEmpty());
        
        digitalKey.setFriendEmail("friend@example.com");
        violations = validator.validate(digitalKey);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should check if key is active")
    void shouldCheckIfKeyIsActive() {
        assertTrue(digitalKey.isActive());
        
        // Suspend key
        digitalKey.setStatus(KeyStatus.SUSPENDED);
        assertFalse(digitalKey.isActive());
        
        // Expire key
        digitalKey.setStatus(KeyStatus.ACTIVE);
        digitalKey.setExpiresAt(LocalDateTime.now().minusDays(1));
        assertFalse(digitalKey.isActive());
        
        // Set usage limit
        digitalKey.setExpiresAt(LocalDateTime.now().plusDays(30));
        digitalKey.setMaxUsageCount(5L);
        digitalKey.setUsageCount(5L);
        assertFalse(digitalKey.isActive());
    }

    @Test
    @DisplayName("Should check expiration status")
    void shouldCheckExpirationStatus() {
        assertFalse(digitalKey.isExpired());
        
        digitalKey.setExpiresAt(LocalDateTime.now().minusDays(1));
        assertTrue(digitalKey.isExpired());
        
        digitalKey.setExpiresAt(LocalDateTime.now().plusDays(1));
        assertFalse(digitalKey.isExpired());
    }

    @Test
    @DisplayName("Should check revocation status")
    void shouldCheckRevocationStatus() {
        assertFalse(digitalKey.isRevoked());
        
        digitalKey.revoke("ADMIN", "Security violation");
        
        assertTrue(digitalKey.isRevoked());
        assertEquals(KeyStatus.TERMINATED, digitalKey.getStatus());
        assertEquals("ADMIN", digitalKey.getRevokedBy());
        assertEquals("Security violation", digitalKey.getRevocationReason());
        assertNotNull(digitalKey.getRevokedAt());
    }

    @Test
    @DisplayName("Should handle usage tracking")
    void shouldHandleUsageTracking() {
        assertEquals(0L, digitalKey.getUsageCount());
        assertNull(digitalKey.getLastUsedAt());
        
        digitalKey.incrementUsage();
        
        assertEquals(1L, digitalKey.getUsageCount());
        assertNotNull(digitalKey.getLastUsedAt());
        assertTrue(digitalKey.getLastUsedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Should handle usage limits")
    void shouldHandleUsageLimits() {
        assertFalse(digitalKey.isUsageLimitReached());
        assertEquals(Long.MAX_VALUE, digitalKey.getRemainingUsages());
        
        digitalKey.setMaxUsageCount(3L);
        assertEquals(3L, digitalKey.getRemainingUsages());
        
        digitalKey.incrementUsage();
        assertEquals(2L, digitalKey.getRemainingUsages());
        
        digitalKey.incrementUsage();
        digitalKey.incrementUsage();
        
        assertTrue(digitalKey.isUsageLimitReached());
        assertEquals(0L, digitalKey.getRemainingUsages());
        assertEquals(KeyStatus.EXPIRED, digitalKey.getStatus());
    }

    @Test
    @DisplayName("Should check valid period")
    void shouldCheckValidPeriod() {
        assertTrue(digitalKey.isWithinValidPeriod());
        
        // Set valid from in future
        digitalKey.setValidFrom(LocalDateTime.now().plusDays(1));
        assertFalse(digitalKey.isWithinValidPeriod());
        
        // Set valid from in past, expires in future
        digitalKey.setValidFrom(LocalDateTime.now().minusDays(1));
        digitalKey.setExpiresAt(LocalDateTime.now().plusDays(1));
        assertTrue(digitalKey.isWithinValidPeriod());
        
        // Set expiration in past
        digitalKey.setExpiresAt(LocalDateTime.now().minusDays(1));
        assertFalse(digitalKey.isWithinValidPeriod());
    }

    @Test
    @DisplayName("Should check if friend key")
    void shouldCheckIfFriendKey() {
        assertFalse(digitalKey.isFriendKey());
        
        digitalKey.setKeyType(KeyType.FRIEND);
        assertTrue(digitalKey.isFriendKey());
    }

    @Test
    @DisplayName("Should activate key")
    void shouldActivateKey() {
        digitalKey.setStatus(KeyStatus.SUSPENDED);
        assertNull(digitalKey.getActivatedAt());
        
        digitalKey.activate();
        
        assertEquals(KeyStatus.ACTIVE, digitalKey.getStatus());
        assertNotNull(digitalKey.getActivatedAt());
        assertNotNull(digitalKey.getValidFrom());
    }

    @Test
    @DisplayName("Should check usage at specific time")
    void shouldCheckUsageAtSpecificTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        LocalDateTime past = now.minusDays(1);
        
        assertTrue(digitalKey.canBeUsedAt(now));
        
        digitalKey.setValidFrom(future);
        assertFalse(digitalKey.canBeUsedAt(now));
        assertTrue(digitalKey.canBeUsedAt(future));
        
        digitalKey.setValidFrom(past);
        digitalKey.setExpiresAt(past);
        assertFalse(digitalKey.canBeUsedAt(now));
    }

    @Test
    @DisplayName("Should handle device information")
    void shouldHandleDeviceInformation() {
        digitalKey.setDeviceModel("iPhone 14");
        digitalKey.setDeviceOs("iOS");
        digitalKey.setDeviceOsVersion("16.0");
        
        Set<ConstraintViolation<DigitalKey>> violations = validator.validate(digitalKey);
        assertTrue(violations.isEmpty());
        
        assertEquals("iPhone 14", digitalKey.getDeviceModel());
        assertEquals("iOS", digitalKey.getDeviceOs());
        assertEquals("16.0", digitalKey.getDeviceOsVersion());
    }

    @Test
    @DisplayName("Should handle friend information")
    void shouldHandleFriendInformation() {
        digitalKey.setKeyType(KeyType.FRIEND);
        digitalKey.setFriendEmail("friend@example.com");
        digitalKey.setFriendName("Jane Smith");
        digitalKey.setFriendPhone("+1234567890");
        
        Set<ConstraintViolation<DigitalKey>> violations = validator.validate(digitalKey);
        assertTrue(violations.isEmpty());
        
        assertEquals("friend@example.com", digitalKey.getFriendEmail());
        assertEquals("Jane Smith", digitalKey.getFriendName());
        assertEquals("+1234567890", digitalKey.getFriendPhone());
    }

    @Test
    @DisplayName("Should handle permission levels")
    void shouldHandlePermissionLevels() {
        assertEquals(PermissionLevel.FULL_ACCESS, digitalKey.getPermissionLevel());
        
        digitalKey.setPermissionLevel(PermissionLevel.DRIVE_ONLY);
        assertEquals(PermissionLevel.DRIVE_ONLY, digitalKey.getPermissionLevel());
        
        digitalKey.setPermissionLevel(PermissionLevel.VALET);
        assertEquals(PermissionLevel.VALET, digitalKey.getPermissionLevel());
    }

    @Test
    @DisplayName("Should handle restrictions")
    void shouldHandleRestrictions() {
        digitalKey.setTimeRestrictions("{\"weekdays\": \"9-17\"}");
        digitalKey.setLocationRestrictions("{\"radius\": 50, \"center\": [37.7749, -122.4194]}");
        
        assertEquals("{\"weekdays\": \"9-17\"}", digitalKey.getTimeRestrictions());
        assertEquals("{\"radius\": 50, \"center\": [37.7749, -122.4194]}", digitalKey.getLocationRestrictions());
    }

    @Test
    @DisplayName("Should update timestamps on modification")
    void shouldUpdateTimestampsOnModification() {
        LocalDateTime originalUpdatedAt = digitalKey.getUpdatedAt();
        
        // Simulate update
        digitalKey.preUpdate();
        
        assertTrue(digitalKey.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}
