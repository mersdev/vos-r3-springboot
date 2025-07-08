package com.vehicleoem.integration;

import com.vehicleoem.config.BaseIntegrationTest;
import com.vehicleoem.config.WireMockConfig;
import com.vehicleoem.dto.*;
import com.vehicleoem.model.*;
import com.vehicleoem.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests using WireMock for external service mocking
 * and Zonky embedded PostgreSQL for database operations
 */
@Import(WireMockConfig.class)
@Transactional
@DisplayName("WireMock Integration Tests")
class WireMockIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OwnerAccountService ownerAccountService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DigitalKeyService digitalKeyService;

    @Autowired
    private KeySharingService keySharingService;

    private OwnerAccount testOwner;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Verify WireMock servers are running
        verifyWireMockServersRunning();

        // Create test data
        testOwner = ownerAccountService.createOwnerAccount(
                "TEST-OWNER-001",
                "test@example.com",
                "John",
                "Doe"
        );

        testVehicle = vehicleService.createVehicle(
                "1HGBH41JXMN109186",
                "Honda",
                "Accord",
                2023,
                testOwner.getAccountId()
        );
    }

    @Test
    @DisplayName("Should successfully track key with valid external service responses")
    void shouldTrackKeyWithValidResponses() {
        // Given
        TrackKeyRequest request = new TrackKeyRequest();
        request.setKeyId("VALID-KEY");
        request.setDeviceId("DEVICE-001");
        request.setDeviceOem("Apple");
        request.setVehicleId(testVehicle.getVin());
        request.setKeyType("OWNER");

        // When
        TrackKeyResponse response = digitalKeyService.trackKey(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("VALID-KEY", response.getKeyId());
        assertNotNull(response.getTrackingId());

        // Verify WireMock interactions
        deviceOemMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/keyValidation"))
                .withRequestBody(containing("VALID-KEY")));
        
        ktsMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/registerKey")));
    }

    @Test
    @DisplayName("Should handle invalid key validation from external service")
    void shouldHandleInvalidKeyValidation() {
        // Given
        TrackKeyRequest request = new TrackKeyRequest();
        request.setKeyId("INVALID-KEY");
        request.setDeviceId("DEVICE-002");
        request.setDeviceOem("Samsung");
        request.setVehicleId(testVehicle.getVin());
        request.setKeyType("FRIEND");

        // When
        TrackKeyResponse response = digitalKeyService.trackKey(request);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Key validation failed"));

        // Verify WireMock interactions
        deviceOemMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/keyValidation"))
                .withRequestBody(containing("INVALID-KEY")));
    }

    @Test
    @DisplayName("Should successfully manage key lifecycle with external service integration")
    void shouldManageKeyLifecycle() {
        // First, track a key
        TrackKeyRequest trackRequest = new TrackKeyRequest();
        trackRequest.setKeyId("LIFECYCLE-KEY");
        trackRequest.setDeviceId("DEVICE-003");
        trackRequest.setDeviceOem("Apple");
        trackRequest.setVehicleId(testVehicle.getVin());
        trackRequest.setKeyType("OWNER");

        TrackKeyResponse trackResponse = digitalKeyService.trackKey(trackRequest);
        assertTrue(trackResponse.isSuccess());

        // Then, suspend the key
        ManageKeyRequest manageRequest = new ManageKeyRequest();
        manageRequest.setKeyId("LIFECYCLE-KEY");
        manageRequest.setAction("SUSPEND");
        manageRequest.setReason("Testing suspension");

        ManageKeyResponse manageResponse = digitalKeyService.manageKey(manageRequest);
        assertTrue(manageResponse.isSuccess());

        // Verify external service calls
        ktsMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/updateKeyStatus"))
                .withRequestBody(containing("LIFECYCLE-KEY")));
    }

    @Test
    @DisplayName("Should handle key sharing with proper external service integration")
    void shouldHandleKeySharing() {
        // First, track an owner key
        TrackKeyRequest trackRequest = new TrackKeyRequest();
        trackRequest.setKeyId("SHARING-KEY");
        trackRequest.setDeviceId("DEVICE-004");
        trackRequest.setDeviceOem("Apple");
        trackRequest.setVehicleId(testVehicle.getVin());
        trackRequest.setKeyType("OWNER");

        digitalKeyService.trackKey(trackRequest);

        // Then, share the key
        ShareKeyRequest shareRequest = new ShareKeyRequest();
        shareRequest.setVehicleVin(testVehicle.getVin());
        shareRequest.setFriendEmail("friend@example.com");
        shareRequest.setFriendName("Jane Smith");
        shareRequest.setPermissionLevel(com.vehicleoem.model.PermissionLevel.DRIVE_ONLY);
        shareRequest.setSharedBy(testOwner.getAccountId());

        ShareKeyResponse shareResponse = keySharingService.shareKey(shareRequest);
        assertTrue(shareResponse.isSuccess());
        assertNotNull(shareResponse.getInvitationCode());

        // Verify event notification was sent
        deviceOemMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/eventNotification")));
    }

    @Test
    @DisplayName("Should handle external service failures gracefully")
    void shouldHandleExternalServiceFailures() {
        // Setup WireMock to return server error
        deviceOemMockServer.stubFor(post(urlEqualTo("/api/v1/keyValidation"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Given
        TrackKeyRequest request = new TrackKeyRequest();
        request.setKeyId("ERROR-KEY");
        request.setDeviceId("DEVICE-005");
        request.setDeviceOem("Apple");
        request.setVehicleId(testVehicle.getVin());
        request.setKeyType("OWNER");

        // When
        TrackKeyResponse response = digitalKeyService.trackKey(request);

        // Then - Should handle gracefully with circuit breaker
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("service") || 
                  response.getMessage().contains("error") ||
                  response.getMessage().contains("failed"));
    }

    @Test
    @DisplayName("Should verify WireMock payload structures")
    void shouldVerifyWireMockPayloads() {
        // Track a key to trigger external service calls
        TrackKeyRequest request = new TrackKeyRequest();
        request.setKeyId("PAYLOAD-TEST-KEY");
        request.setDeviceId("DEVICE-006");
        request.setDeviceOem("Samsung");
        request.setVehicleId(testVehicle.getVin());
        request.setKeyType("FRIEND");

        digitalKeyService.trackKey(request);

        // Verify the exact payload structure sent to Device OEM service
        deviceOemMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/keyValidation"))
                .withRequestBody(matchingJsonPath("$.keyId", equalTo("PAYLOAD-TEST-KEY")))
                .withRequestBody(matchingJsonPath("$.deviceId", equalTo("DEVICE-006")))
                .withRequestBody(matchingJsonPath("$.deviceOem", equalTo("Samsung")))
                .withHeader("Content-Type", equalTo("application/json")));

        // Verify the payload structure sent to KTS service
        ktsMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/registerKey"))
                .withRequestBody(matchingJsonPath("$.keyId", equalTo("PAYLOAD-TEST-KEY")))
                .withRequestBody(matchingJsonPath("$.vehicleId", equalTo(testVehicle.getVin())))
                .withRequestBody(matchingJsonPath("$.keyType", equalTo("FRIEND")))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    @DisplayName("Should test database operations with Zonky embedded PostgreSQL")
    void shouldTestDatabaseOperations() {
        // Verify we're using PostgreSQL dialect
        String jdbcUrl = getEmbeddedJdbcUrl();
        assertTrue(jdbcUrl.contains("postgresql"));

        // Test complex database operations
        OwnerAccount owner2 = ownerAccountService.createOwnerAccount(
                "TEST-OWNER-002",
                "owner2@example.com",
                "Jane",
                "Smith"
        );

        Vehicle vehicle2 = vehicleService.createVehicle(
                "2HGBH41JXMN109187",
                "Toyota",
                "Camry",
                2023,
                owner2.getAccountId()
        );

        // Verify PostgreSQL-specific features work
        assertNotNull(owner2.getId());
        assertNotNull(vehicle2.getId());
        assertEquals("TEST-OWNER-002", owner2.getAccountId());
        assertEquals("2HGBH41JXMN109187", vehicle2.getVin());
    }
}
