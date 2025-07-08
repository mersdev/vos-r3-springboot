package com.vehicleoem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicleoem.dto.*;
import com.vehicleoem.service.DigitalKeyService;
import com.vehicleoem.exception.VehicleNotFoundException;
import com.vehicleoem.exception.KeyManagementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.vehicleoem.config.TestSecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DigitalKeyController.class)
@Import(TestSecurityConfig.class)
@DisplayName("DigitalKeyController Integration Tests")
class DigitalKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DigitalKeyService digitalKeyService;

    @Autowired
    private ObjectMapper objectMapper;

    private TrackKeyRequest trackKeyRequest;
    private ManageKeyRequest manageKeyRequest;

    @BeforeEach
    void setUp() {
        trackKeyRequest = new TrackKeyRequest();
        trackKeyRequest.setKeyId("TEST-KEY-001");
        trackKeyRequest.setDeviceId("DEVICE-001");
        trackKeyRequest.setDeviceOem("Apple");
        trackKeyRequest.setVehicleId("1HGBH41JXMN109186");
        trackKeyRequest.setKeyType("OWNER");

        manageKeyRequest = new ManageKeyRequest();
        manageKeyRequest.setKeyId("TEST-KEY-001");
        manageKeyRequest.setAction("SUSPEND");
        manageKeyRequest.setReason("Security concern");
        manageKeyRequest.setRequestedBy("ADMIN");
    }

    @Test
    @DisplayName("Should successfully track key")
    void shouldSuccessfullyTrackKey() throws Exception {
        // Arrange
        TrackKeyResponse response = new TrackKeyResponse(true, "Key tracked successfully", "TEST-KEY-001", "TEST-KEY-001");
        when(digitalKeyService.trackKey(any(TrackKeyRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Key tracked successfully"))
                .andExpect(jsonPath("$.keyId").value("TEST-KEY-001"))
                .andExpect(jsonPath("$.trackingId").value("TEST-KEY-001"));
    }

    @Test
    @DisplayName("Should handle track key validation errors")
    void shouldHandleTrackKeyValidationErrors() throws Exception {
        // Arrange - Invalid request with missing required fields
        trackKeyRequest.setKeyId("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle vehicle not found error")
    void shouldHandleVehicleNotFoundError() throws Exception {
        // Arrange
        when(digitalKeyService.trackKey(any(TrackKeyRequest.class)))
                .thenThrow(new VehicleNotFoundException("1HGBH41JXMN109186"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("VEHICLE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Vehicle not found with VIN: 1HGBH41JXMN109186"));
    }

    @Test
    @DisplayName("Should successfully manage key")
    void shouldSuccessfullyManageKey() throws Exception {
        // Arrange
        ManageKeyResponse response = new ManageKeyResponse(true, "Key managed successfully", "TEST-KEY-001", "SUSPENDED");
        when(digitalKeyService.manageKey(any(ManageKeyRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageKeyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Key managed successfully"))
                .andExpect(jsonPath("$.keyId").value("TEST-KEY-001"))
                .andExpect(jsonPath("$.newStatus").value("SUSPENDED"));
    }

    @Test
    @DisplayName("Should handle manage key validation errors")
    void shouldHandleManageKeyValidationErrors() throws Exception {
        // Arrange - Invalid request with missing required fields
        manageKeyRequest.setKeyId("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageKeyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle key not found error")
    void shouldHandleKeyNotFoundError() throws Exception {
        // Arrange
        when(digitalKeyService.manageKey(any(ManageKeyRequest.class)))
                .thenThrow(KeyManagementException.keyNotFound("TEST-KEY-001"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageKeyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("KEY_MANAGEMENT_ERROR"))
                .andExpect(jsonPath("$.message").value("Digital key not found: TEST-KEY-001"));
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void shouldHandleInvalidJsonFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing content type")
    void shouldHandleMissingContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should handle friend key tracking")
    void shouldHandleFriendKeyTracking() throws Exception {
        // Arrange
        trackKeyRequest.setKeyType("FRIEND");
        trackKeyRequest.setFriendEmail("friend@example.com");
        
        TrackKeyResponse response = new TrackKeyResponse(true, "Friend key tracked successfully", "TEST-KEY-001", "TEST-KEY-001");
        when(digitalKeyService.trackKey(any(TrackKeyRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Friend key tracked successfully"));
    }

    @Test
    @DisplayName("Should handle different manage key actions")
    void shouldHandleDifferentManageKeyActions() throws Exception {
        // Test RESUME action
        manageKeyRequest.setAction("RESUME");
        ManageKeyResponse resumeResponse = new ManageKeyResponse(true, "Key resumed successfully", "TEST-KEY-001", "ACTIVE");
        when(digitalKeyService.manageKey(any(ManageKeyRequest.class))).thenReturn(resumeResponse);

        mockMvc.perform(post("/api/v1/keys/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageKeyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newStatus").value("ACTIVE"));

        // Test TERMINATE action
        manageKeyRequest.setAction("TERMINATE");
        ManageKeyResponse terminateResponse = new ManageKeyResponse(true, "Key terminated successfully", "TEST-KEY-001", "TERMINATED");
        when(digitalKeyService.manageKey(any(ManageKeyRequest.class))).thenReturn(terminateResponse);

        mockMvc.perform(post("/api/v1/keys/manage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageKeyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newStatus").value("TERMINATED"));
    }

    @Test
    @DisplayName("Should handle internal server errors")
    void shouldHandleInternalServerErrors() throws Exception {
        // Arrange
        when(digitalKeyService.trackKey(any(TrackKeyRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/keys/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackKeyRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"));
    }
}
