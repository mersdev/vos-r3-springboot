package com.vehicleoem.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vehicleoem.model.PermissionLevel;
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

@DisplayName("ShareKeyRequest DTO Tests")
class ShareKeyRequestTest {

    private Validator validator;
    private ObjectMapper objectMapper;
    private ShareKeyRequest shareKeyRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        shareKeyRequest = new ShareKeyRequest();
        shareKeyRequest.setVehicleVin("1HGBH41JXMN109186");
        shareKeyRequest.setFriendEmail("friend@example.com");
        shareKeyRequest.setSharedBy("owner@example.com");
    }

    @Test
    @DisplayName("Should validate valid ShareKeyRequest")
    void shouldValidateValidShareKeyRequest() {
        Set<ConstraintViolation<ShareKeyRequest>> violations = validator.validate(shareKeyRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmailFormat() {
        shareKeyRequest.setFriendEmail("invalid-email");
        Set<ConstraintViolation<ShareKeyRequest>> violations = validator.validate(shareKeyRequest);
        assertFalse(violations.isEmpty());
        
        shareKeyRequest.setFriendEmail("valid@example.com");
        violations = validator.validate(shareKeyRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty required fields")
    void shouldRejectEmptyRequiredFields() {
        shareKeyRequest.setVehicleVin("");
        Set<ConstraintViolation<ShareKeyRequest>> violations = validator.validate(shareKeyRequest);
        assertFalse(violations.isEmpty());
        
        shareKeyRequest.setVehicleVin("1HGBH41JXMN109186");
        shareKeyRequest.setFriendEmail("");
        violations = validator.validate(shareKeyRequest);
        assertFalse(violations.isEmpty());
        
        shareKeyRequest.setFriendEmail("friend@example.com");
        shareKeyRequest.setSharedBy("");
        violations = validator.validate(shareKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle permission levels")
    void shouldHandlePermissionLevels() {
        assertEquals(PermissionLevel.DRIVE_ONLY, shareKeyRequest.getPermissionLevel());
        
        shareKeyRequest.setPermissionLevel(PermissionLevel.FULL_ACCESS);
        assertEquals(PermissionLevel.FULL_ACCESS, shareKeyRequest.getPermissionLevel());
        
        shareKeyRequest.setPermissionLevel(PermissionLevel.VALET);
        assertEquals(PermissionLevel.VALET, shareKeyRequest.getPermissionLevel());
    }

    @Test
    @DisplayName("Should handle optional fields")
    void shouldHandleOptionalFields() {
        shareKeyRequest.setFriendName("Jane Smith");
        shareKeyRequest.setFriendPhone("+1234567890");
        shareKeyRequest.setExpiresAt(LocalDateTime.now().plusDays(30));
        shareKeyRequest.setTimeRestrictions("{\"weekdays\": \"9-17\"}");
        shareKeyRequest.setLocationRestrictions("{\"radius\": 50}");
        shareKeyRequest.setMaxUsageCount(100L);
        shareKeyRequest.setMessage("Welcome to my car!");
        
        Set<ConstraintViolation<ShareKeyRequest>> violations = validator.validate(shareKeyRequest);
        assertTrue(violations.isEmpty());
        
        assertEquals("Jane Smith", shareKeyRequest.getFriendName());
        assertEquals("+1234567890", shareKeyRequest.getFriendPhone());
        assertNotNull(shareKeyRequest.getExpiresAt());
        assertEquals("{\"weekdays\": \"9-17\"}", shareKeyRequest.getTimeRestrictions());
        assertEquals("{\"radius\": 50}", shareKeyRequest.getLocationRestrictions());
        assertEquals(100L, shareKeyRequest.getMaxUsageCount());
        assertEquals("Welcome to my car!", shareKeyRequest.getMessage());
    }

    @Test
    @DisplayName("Should serialize to JSON correctly")
    void shouldSerializeToJsonCorrectly() throws Exception {
        shareKeyRequest.setFriendName("Jane Smith");
        shareKeyRequest.setPermissionLevel(PermissionLevel.DRIVE_ONLY);
        shareKeyRequest.setMaxUsageCount(50L);
        
        String json = objectMapper.writeValueAsString(shareKeyRequest);
        
        assertTrue(json.contains("\"vehicleVin\":\"1HGBH41JXMN109186\""));
        assertTrue(json.contains("\"friendEmail\":\"friend@example.com\""));
        assertTrue(json.contains("\"friendName\":\"Jane Smith\""));
        assertTrue(json.contains("\"permissionLevel\":\"DRIVE_ONLY\""));
        assertTrue(json.contains("\"maxUsageCount\":50"));
        assertTrue(json.contains("\"sharedBy\":\"owner@example.com\""));
    }

    @Test
    @DisplayName("Should deserialize from JSON correctly")
    void shouldDeserializeFromJsonCorrectly() throws Exception {
        String json = """
            {
                "vehicleVin": "1HGBH41JXMN109187",
                "friendEmail": "json-friend@example.com",
                "friendName": "JSON Friend",
                "friendPhone": "+9876543210",
                "permissionLevel": "UNLOCK_ONLY",
                "timeRestrictions": "{\\"hours\\": \\"9-17\\"}",
                "locationRestrictions": "{\\"radius\\": 25}",
                "maxUsageCount": 25,
                "sharedBy": "json-owner@example.com",
                "message": "JSON message"
            }
            """;
        
        ShareKeyRequest deserialized = objectMapper.readValue(json, ShareKeyRequest.class);
        
        assertEquals("1HGBH41JXMN109187", deserialized.getVehicleVin());
        assertEquals("json-friend@example.com", deserialized.getFriendEmail());
        assertEquals("JSON Friend", deserialized.getFriendName());
        assertEquals("+9876543210", deserialized.getFriendPhone());
        assertEquals(PermissionLevel.UNLOCK_ONLY, deserialized.getPermissionLevel());
        assertEquals("{\"hours\": \"9-17\"}", deserialized.getTimeRestrictions());
        assertEquals("{\"radius\": 25}", deserialized.getLocationRestrictions());
        assertEquals(25L, deserialized.getMaxUsageCount());
        assertEquals("json-owner@example.com", deserialized.getSharedBy());
        assertEquals("JSON message", deserialized.getMessage());
    }

    @Test
    @DisplayName("Should handle date time serialization")
    void shouldHandleDateTimeSerialization() throws Exception {
        LocalDateTime expirationDate = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        shareKeyRequest.setExpiresAt(expirationDate);
        
        String json = objectMapper.writeValueAsString(shareKeyRequest);
        ShareKeyRequest deserialized = objectMapper.readValue(json, ShareKeyRequest.class);
        
        assertEquals(expirationDate, deserialized.getExpiresAt());
    }

    @Test
    @DisplayName("Should validate with all fields populated")
    void shouldValidateWithAllFieldsPopulated() {
        shareKeyRequest.setFriendName("Complete Friend");
        shareKeyRequest.setFriendPhone("+1234567890");
        shareKeyRequest.setPermissionLevel(PermissionLevel.FULL_ACCESS);
        shareKeyRequest.setExpiresAt(LocalDateTime.now().plusDays(30));
        shareKeyRequest.setTimeRestrictions("{\"weekdays\": \"9-17\", \"weekends\": \"10-16\"}");
        shareKeyRequest.setLocationRestrictions("{\"radius\": 100, \"center\": [37.7749, -122.4194]}");
        shareKeyRequest.setMaxUsageCount(200L);
        shareKeyRequest.setMessage("Complete access to my vehicle with restrictions");
        
        Set<ConstraintViolation<ShareKeyRequest>> violations = validator.validate(shareKeyRequest);
        assertTrue(violations.isEmpty());
    }
}
