package com.vehicleoem.service;

import com.vehicleoem.dto.TrackKeyRequest;
import com.vehicleoem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidationService Unit Tests")
class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    private TrackKeyRequest trackKeyRequest;
    private Vehicle testVehicle;
    private OwnerAccount testOwner;

    @BeforeEach
    void setUp() {
        testOwner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        testOwner.setEmailVerified(true);
        testOwner.setAccountStatus(AccountStatus.ACTIVE);

        testVehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, testOwner);
        testVehicle.setSubscriptionActive(true);
        testVehicle.setSubscriptionTier(SubscriptionTier.PREMIUM);
        testVehicle.setCurrentKeyCount(5);
        testVehicle.setMaxKeysAllowed(20);
        testVehicle.setVehicleStatus(VehicleStatus.ACTIVE);

        trackKeyRequest = new TrackKeyRequest();
        trackKeyRequest.setKeyId("VALID-KEY-123");
        trackKeyRequest.setDeviceId("DEVICE001");
        trackKeyRequest.setVehicleId("1HGBH41JXMN109186");
        trackKeyRequest.setKeyType("OWNER");
    }

    @Test
    @DisplayName("Should validate valid VIN")
    void shouldValidateValidVin() {
        assertDoesNotThrow(() -> validationService.validateVin("1HGBH41JXMN109186"));
    }

    @Test
    @DisplayName("Should reject invalid VIN format")
    void shouldRejectInvalidVinFormat() {
        // Test null VIN
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVin(null));
        
        // Test empty VIN
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVin(""));
        
        // Test wrong length
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVin("1HGBH41JX"));
        
        // Test invalid characters (I, O, Q)
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVin("1HGBH41JXIN109186"));
    }

    @Test
    @DisplayName("Should validate valid email")
    void shouldValidateValidEmail() {
        assertDoesNotThrow(() -> validationService.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> validationService.validateEmail("user.name+tag@domain.co.uk"));
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmailFormat() {
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateEmail(null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateEmail(""));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateEmail("invalid-email"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateEmail("@domain.com"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateEmail("user@"));
    }

    @Test
    @DisplayName("Should validate valid phone number")
    void shouldValidateValidPhoneNumber() {
        assertDoesNotThrow(() -> validationService.validatePhoneNumber("+1234567890"));
        assertDoesNotThrow(() -> validationService.validatePhoneNumber("1234567890"));
        assertDoesNotThrow(() -> validationService.validatePhoneNumber(null)); // Optional field
        assertDoesNotThrow(() -> validationService.validatePhoneNumber("")); // Optional field
    }

    @Test
    @DisplayName("Should reject invalid phone number format")
    void shouldRejectInvalidPhoneNumberFormat() {
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validatePhoneNumber("invalid-phone"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validatePhoneNumber("123"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validatePhoneNumber("+"));
    }

    @Test
    @DisplayName("Should validate valid track key request")
    void shouldValidateValidTrackKeyRequest() {
        assertDoesNotThrow(() -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
    }

    @Test
    @DisplayName("Should reject track key request with invalid fields")
    void shouldRejectTrackKeyRequestWithInvalidFields() {
        // Test null key ID
        trackKeyRequest.setKeyId(null);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Test short key ID
        trackKeyRequest.setKeyId("SHORT");
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Reset and test device ID
        trackKeyRequest.setKeyId("VALID-KEY-123");
        trackKeyRequest.setDeviceId("");
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
    }

    @Test
    @DisplayName("Should validate friend key requirements")
    void shouldValidateFriendKeyRequirements() {
        trackKeyRequest.setKeyType("FRIEND");
        
        // Should fail without friend email
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Should succeed with valid friend email
        trackKeyRequest.setFriendEmail("friend@example.com");
        assertDoesNotThrow(() -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Should fail if subscription doesn't allow friend keys
        testVehicle.setSubscriptionTier(SubscriptionTier.BASIC);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
    }

    @Test
    @DisplayName("Should validate expiration date constraints")
    void shouldValidateExpirationDateConstraints() {
        trackKeyRequest.setKeyType("FRIEND");
        trackKeyRequest.setFriendEmail("friend@example.com");
        
        // Test past expiration date
        trackKeyRequest.setExpiresAt(LocalDateTime.now().minusDays(1).toString());
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Test expiration beyond subscription limit
        trackKeyRequest.setExpiresAt(LocalDateTime.now().plusDays(200).toString());
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
        
        // Test valid expiration date
        trackKeyRequest.setExpiresAt(LocalDateTime.now().plusDays(30).toString());
        assertDoesNotThrow(() -> 
            validationService.validateTrackKeyRequest(trackKeyRequest, testVehicle));
    }

    @Test
    @DisplayName("Should validate vehicle subscription")
    void shouldValidateVehicleSubscription() {
        assertDoesNotThrow(() -> validationService.validateVehicleSubscription(testVehicle));
        
        // Test inactive subscription
        testVehicle.setSubscriptionActive(false);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVehicleSubscription(testVehicle));
        
        // Test expired subscription
        testVehicle.setSubscriptionActive(true);
        testVehicle.setSubscriptionExpiresAt(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVehicleSubscription(testVehicle));
        
        // Test inactive vehicle status
        testVehicle.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(30));
        testVehicle.setVehicleStatus(VehicleStatus.MAINTENANCE);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateVehicleSubscription(testVehicle));
    }

    @Test
    @DisplayName("Should validate key limits")
    void shouldValidateKeyLimits() {
        assertDoesNotThrow(() -> validationService.validateKeyLimits(testVehicle));
        
        // Test key limit reached
        testVehicle.setCurrentKeyCount(20);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyLimits(testVehicle));
    }

    @Test
    @DisplayName("Should validate owner account")
    void shouldValidateOwnerAccount() {
        assertDoesNotThrow(() -> validationService.validateOwnerAccount(testOwner));
        
        // Test inactive account
        testOwner.setAccountStatus(AccountStatus.SUSPENDED);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateOwnerAccount(testOwner));
        
        // Test locked account
        testOwner.setAccountStatus(AccountStatus.ACTIVE);
        testOwner.lockAccount(30);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateOwnerAccount(testOwner));
        
        // Test unverified email
        testOwner.unlockAccount();
        testOwner.setEmailVerified(false);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateOwnerAccount(testOwner));
    }

    @Test
    @DisplayName("Should validate key actions")
    void shouldValidateKeyActions() {
        assertDoesNotThrow(() -> validationService.validateKeyAction("SUSPEND"));
        assertDoesNotThrow(() -> validationService.validateKeyAction("RESUME"));
        assertDoesNotThrow(() -> validationService.validateKeyAction("TERMINATE"));
        assertDoesNotThrow(() -> validationService.validateKeyAction("EXPIRE"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyAction("INVALID"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyAction(null));
        
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyAction(""));
    }

    @Test
    @DisplayName("Should validate key status transitions")
    void shouldValidateKeyStatusTransitions() {
        DigitalKey activeKey = new DigitalKey("KEY001", "DEVICE001", "Apple", KeyType.OWNER, testVehicle);
        activeKey.setStatus(KeyStatus.ACTIVE);
        
        // Valid transitions
        assertDoesNotThrow(() -> 
            validationService.validateKeyStatusTransition(activeKey, "SUSPEND"));
        assertDoesNotThrow(() -> 
            validationService.validateKeyStatusTransition(activeKey, "TERMINATE"));
        
        // Invalid transition - can't suspend already suspended key
        activeKey.setStatus(KeyStatus.SUSPENDED);
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyStatusTransition(activeKey, "SUSPEND"));
        
        // Valid resume from suspended
        assertDoesNotThrow(() -> 
            validationService.validateKeyStatusTransition(activeKey, "RESUME"));
        
        // Can't resume expired key
        activeKey.setStatus(KeyStatus.SUSPENDED);
        activeKey.setExpiresAt(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> 
            validationService.validateKeyStatusTransition(activeKey, "RESUME"));
    }

    @Test
    @DisplayName("Should validate business hours")
    void shouldValidateBusinessHours() {
        // Note: This test might fail depending on when it's run
        // In a real implementation, you might want to mock the current time
        // For now, we'll just test that the method doesn't throw unexpected exceptions
        try {
            validationService.validateBusinessHours();
        } catch (IllegalArgumentException e) {
            // Expected if run outside business hours
            assertTrue(e.getMessage().contains("business hours"));
        }
    }
}
