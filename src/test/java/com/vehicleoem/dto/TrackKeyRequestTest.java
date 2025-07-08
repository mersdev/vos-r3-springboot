package com.vehicleoem.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrackKeyRequest DTO Tests")
class TrackKeyRequestTest {

    private Validator validator;
    private ObjectMapper objectMapper;
    private TrackKeyRequest trackKeyRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
        
        trackKeyRequest = new TrackKeyRequest();
        trackKeyRequest.setKeyId("TEST-KEY-001");
        trackKeyRequest.setDeviceId("DEVICE-001");
        trackKeyRequest.setDeviceOem("Apple");
        trackKeyRequest.setVehicleId("1HGBH41JXMN109186");
        trackKeyRequest.setKeyType("OWNER");
    }

    @Test
    @DisplayName("Should validate valid TrackKeyRequest")
    void shouldValidateValidTrackKeyRequest() {
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty key ID")
    void shouldRejectEmptyKeyId() {
        trackKeyRequest.setKeyId("");
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
        
        trackKeyRequest.setKeyId(null);
        violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty device ID")
    void shouldRejectEmptyDeviceId() {
        trackKeyRequest.setDeviceId("");
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
        
        trackKeyRequest.setDeviceId(null);
        violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty device OEM")
    void shouldRejectEmptyDeviceOem() {
        trackKeyRequest.setDeviceOem("");
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
        
        trackKeyRequest.setDeviceOem(null);
        violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty vehicle ID")
    void shouldRejectEmptyVehicleId() {
        trackKeyRequest.setVehicleId("");
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
        
        trackKeyRequest.setVehicleId(null);
        violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject empty key type")
    void shouldRejectEmptyKeyType() {
        trackKeyRequest.setKeyType("");
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
        
        trackKeyRequest.setKeyType(null);
        violations = validator.validate(trackKeyRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle optional fields")
    void shouldHandleOptionalFields() {
        trackKeyRequest.setPublicKey("test-public-key");
        trackKeyRequest.setUiBundle("test-ui-bundle");
        trackKeyRequest.setVehicleMobilizationData("test-mobilization-data");
        trackKeyRequest.setFriendEmail("friend@example.com");
        trackKeyRequest.setExpiresAt("2024-12-31T23:59:59");
        
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(trackKeyRequest);
        assertTrue(violations.isEmpty());
        
        assertEquals("test-public-key", trackKeyRequest.getPublicKey());
        assertEquals("test-ui-bundle", trackKeyRequest.getUiBundle());
        assertEquals("test-mobilization-data", trackKeyRequest.getVehicleMobilizationData());
        assertEquals("friend@example.com", trackKeyRequest.getFriendEmail());
        assertEquals("2024-12-31T23:59:59", trackKeyRequest.getExpiresAt());
    }

    @Test
    @DisplayName("Should serialize to JSON correctly")
    void shouldSerializeToJsonCorrectly() throws Exception {
        trackKeyRequest.setPublicKey("test-public-key");
        trackKeyRequest.setFriendEmail("friend@example.com");
        
        String json = objectMapper.writeValueAsString(trackKeyRequest);
        
        assertTrue(json.contains("\"keyId\":\"TEST-KEY-001\""));
        assertTrue(json.contains("\"deviceId\":\"DEVICE-001\""));
        assertTrue(json.contains("\"deviceOem\":\"Apple\""));
        assertTrue(json.contains("\"vehicleId\":\"1HGBH41JXMN109186\""));
        assertTrue(json.contains("\"keyType\":\"OWNER\""));
        assertTrue(json.contains("\"publicKey\":\"test-public-key\""));
        assertTrue(json.contains("\"friendEmail\":\"friend@example.com\""));
    }

    @Test
    @DisplayName("Should deserialize from JSON correctly")
    void shouldDeserializeFromJsonCorrectly() throws Exception {
        String json = """
            {
                "keyId": "JSON-KEY-001",
                "deviceId": "JSON-DEVICE-001",
                "deviceOem": "Samsung",
                "vehicleId": "1HGBH41JXMN109187",
                "keyType": "FRIEND",
                "publicKey": "json-public-key",
                "friendEmail": "json-friend@example.com"
            }
            """;
        
        TrackKeyRequest deserialized = objectMapper.readValue(json, TrackKeyRequest.class);
        
        assertEquals("JSON-KEY-001", deserialized.getKeyId());
        assertEquals("JSON-DEVICE-001", deserialized.getDeviceId());
        assertEquals("Samsung", deserialized.getDeviceOem());
        assertEquals("1HGBH41JXMN109187", deserialized.getVehicleId());
        assertEquals("FRIEND", deserialized.getKeyType());
        assertEquals("json-public-key", deserialized.getPublicKey());
        assertEquals("json-friend@example.com", deserialized.getFriendEmail());
    }

    @Test
    @DisplayName("Should handle null optional fields in JSON")
    void shouldHandleNullOptionalFieldsInJson() throws Exception {
        String json = """
            {
                "keyId": "MINIMAL-KEY-001",
                "deviceId": "MINIMAL-DEVICE-001",
                "deviceOem": "Google",
                "vehicleId": "1HGBH41JXMN109188",
                "keyType": "OWNER"
            }
            """;
        
        TrackKeyRequest deserialized = objectMapper.readValue(json, TrackKeyRequest.class);
        
        assertEquals("MINIMAL-KEY-001", deserialized.getKeyId());
        assertEquals("MINIMAL-DEVICE-001", deserialized.getDeviceId());
        assertEquals("Google", deserialized.getDeviceOem());
        assertEquals("1HGBH41JXMN109188", deserialized.getVehicleId());
        assertEquals("OWNER", deserialized.getKeyType());
        assertNull(deserialized.getPublicKey());
        assertNull(deserialized.getUiBundle());
        assertNull(deserialized.getVehicleMobilizationData());
        assertNull(deserialized.getFriendEmail());
        assertNull(deserialized.getExpiresAt());
        
        Set<ConstraintViolation<TrackKeyRequest>> violations = validator.validate(deserialized);
        assertTrue(violations.isEmpty());
    }
}
