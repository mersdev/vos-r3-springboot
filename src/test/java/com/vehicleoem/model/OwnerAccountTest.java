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

@DisplayName("OwnerAccount Model Tests")
class OwnerAccountTest {

    private Validator validator;
    private OwnerAccount ownerAccount;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        ownerAccount = new OwnerAccount("TEST001", "test@example.com", "John", "Doe");
    }

    @Test
    @DisplayName("Should create valid OwnerAccount")
    void shouldCreateValidOwnerAccount() {
        Set<ConstraintViolation<OwnerAccount>> violations = validator.validate(ownerAccount);
        assertTrue(violations.isEmpty());
        
        assertEquals("TEST001", ownerAccount.getAccountId());
        assertEquals("test@example.com", ownerAccount.getEmail());
        assertEquals("John", ownerAccount.getFirstName());
        assertEquals("Doe", ownerAccount.getLastName());
        assertEquals(AccountStatus.ACTIVE, ownerAccount.getAccountStatus());
        assertFalse(ownerAccount.getEmailVerified());
        assertFalse(ownerAccount.getPhoneVerified());
        assertEquals(0, ownerAccount.getFailedLoginAttempts());
        assertNotNull(ownerAccount.getCreatedAt());
        assertNotNull(ownerAccount.getUpdatedAt());
    }

    @Test
    @DisplayName("Should validate account ID format")
    void shouldValidateAccountIdFormat() {
        // Test invalid account ID
        ownerAccount.setAccountId("invalid-id");
        Set<ConstraintViolation<OwnerAccount>> violations = validator.validate(ownerAccount);
        assertFalse(violations.isEmpty());
        
        // Test valid account ID
        ownerAccount.setAccountId("VALID123");
        violations = validator.validate(ownerAccount);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        // Test invalid email
        ownerAccount.setEmail("invalid-email");
        Set<ConstraintViolation<OwnerAccount>> violations = validator.validate(ownerAccount);
        assertFalse(violations.isEmpty());
        
        // Test valid email
        ownerAccount.setEmail("valid@example.com");
        violations = validator.validate(ownerAccount);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate name format")
    void shouldValidateNameFormat() {
        // Test invalid first name with numbers
        ownerAccount.setFirstName("John123");
        Set<ConstraintViolation<OwnerAccount>> violations = validator.validate(ownerAccount);
        assertFalse(violations.isEmpty());
        
        // Test valid name with apostrophe
        ownerAccount.setFirstName("O'Connor");
        ownerAccount.setLastName("Smith-Jones");
        violations = validator.validate(ownerAccount);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle account locking correctly")
    void shouldHandleAccountLocking() {
        assertFalse(ownerAccount.isAccountLocked());
        
        // Lock account for 30 minutes
        ownerAccount.lockAccount(30);
        
        assertTrue(ownerAccount.isAccountLocked());
        assertEquals(AccountStatus.LOCKED, ownerAccount.getAccountStatus());
        assertNotNull(ownerAccount.getAccountLockedUntil());
        assertTrue(ownerAccount.getAccountLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should handle failed login attempts")
    void shouldHandleFailedLoginAttempts() {
        assertEquals(0, ownerAccount.getFailedLoginAttempts());
        
        // Increment failed attempts
        for (int i = 1; i < 5; i++) {
            ownerAccount.incrementFailedLoginAttempts();
            assertEquals(i, ownerAccount.getFailedLoginAttempts());
            assertFalse(ownerAccount.isAccountLocked());
        }
        
        // 5th attempt should lock the account
        ownerAccount.incrementFailedLoginAttempts();
        assertEquals(5, ownerAccount.getFailedLoginAttempts());
        assertTrue(ownerAccount.isAccountLocked());
        assertEquals(AccountStatus.LOCKED, ownerAccount.getAccountStatus());
    }

    @Test
    @DisplayName("Should unlock account correctly")
    void shouldUnlockAccount() {
        // Lock account first
        ownerAccount.lockAccount(30);
        assertTrue(ownerAccount.isAccountLocked());
        
        // Unlock account
        ownerAccount.unlockAccount();
        
        assertFalse(ownerAccount.isAccountLocked());
        assertEquals(0, ownerAccount.getFailedLoginAttempts());
        assertNull(ownerAccount.getAccountLockedUntil());
        assertEquals(AccountStatus.ACTIVE, ownerAccount.getAccountStatus());
    }

    @Test
    @DisplayName("Should check if account is active")
    void shouldCheckIfAccountIsActive() {
        assertTrue(ownerAccount.isAccountActive());
        
        // Suspend account
        ownerAccount.setAccountStatus(AccountStatus.SUSPENDED);
        assertFalse(ownerAccount.isAccountActive());
        
        // Lock account
        ownerAccount.setAccountStatus(AccountStatus.ACTIVE);
        ownerAccount.lockAccount(30);
        assertFalse(ownerAccount.isAccountActive());
    }

    @Test
    @DisplayName("Should handle contact information")
    void shouldHandleContactInformation() {
        ownerAccount.setPhoneNumber("+1234567890");
        ownerAccount.setAddress("123 Main St");
        ownerAccount.setCity("Anytown");
        ownerAccount.setState("CA");
        ownerAccount.setZipCode("12345");
        ownerAccount.setCountry("USA");
        
        Set<ConstraintViolation<OwnerAccount>> violations = validator.validate(ownerAccount);
        assertTrue(violations.isEmpty());
        
        assertEquals("+1234567890", ownerAccount.getPhoneNumber());
        assertEquals("123 Main St", ownerAccount.getAddress());
        assertEquals("Anytown", ownerAccount.getCity());
        assertEquals("CA", ownerAccount.getState());
        assertEquals("12345", ownerAccount.getZipCode());
        assertEquals("USA", ownerAccount.getCountry());
    }

    @Test
    @DisplayName("Should update timestamps on modification")
    void shouldUpdateTimestampsOnModification() {
        LocalDateTime originalUpdatedAt = ownerAccount.getUpdatedAt();
        
        // Simulate update
        ownerAccount.preUpdate();
        
        assertTrue(ownerAccount.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should handle verification status")
    void shouldHandleVerificationStatus() {
        assertFalse(ownerAccount.getEmailVerified());
        assertFalse(ownerAccount.getPhoneVerified());
        
        ownerAccount.setEmailVerified(true);
        ownerAccount.setPhoneVerified(true);
        
        assertTrue(ownerAccount.getEmailVerified());
        assertTrue(ownerAccount.getPhoneVerified());
    }

    @Test
    @DisplayName("Should handle last login tracking")
    void shouldHandleLastLoginTracking() {
        assertNull(ownerAccount.getLastLoginAt());
        
        LocalDateTime loginTime = LocalDateTime.now();
        ownerAccount.setLastLoginAt(loginTime);
        
        assertEquals(loginTime, ownerAccount.getLastLoginAt());
    }
}
