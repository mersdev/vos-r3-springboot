package com.vehicleoem.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestConfiguration
public class WireMockConfig {

    @Bean
    @Primary
    public WireMockServer deviceOemMockServer() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9001));
        wireMockServer.start();
        setupDeviceOemStubs(wireMockServer);
        return wireMockServer;
    }

    @Bean
    @Primary
    public WireMockServer ktsMockServer() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9002));
        wireMockServer.start();
        setupKtsStubs(wireMockServer);
        return wireMockServer;
    }

    @Bean
    @Primary
    public WireMockServer telematicsMockServer() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(9003));
        wireMockServer.start();
        setupTelematicsStubs(wireMockServer);
        return wireMockServer;
    }

    private void setupDeviceOemStubs(WireMockServer server) {
        // Event Notification endpoint
        server.stubFor(post(urlEqualTo("/api/v1/eventNotification"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\",\"message\":\"Event notification received\"}")));

        // Key Validation endpoint - Success case
        server.stubFor(post(urlEqualTo("/api/v1/keyValidation"))
                .withRequestBody(containing("\"keyId\":\"VALID-KEY\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"valid\": true,\n" +
                                "  \"keyId\": \"VALID-KEY\",\n" +
                                "  \"deviceId\": \"DEVICE-001\",\n" +
                                "  \"status\": \"ACTIVE\",\n" +
                                "  \"expirationDate\": \"2024-12-31T23:59:59Z\",\n" +
                                "  \"permissions\": [\"UNLOCK\", \"LOCK\", \"START_ENGINE\"]\n" +
                                "}")));

        // Key Validation endpoint - Invalid key case
        server.stubFor(post(urlEqualTo("/api/v1/keyValidation"))
                .withRequestBody(containing("\"keyId\":\"INVALID-KEY\""))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"valid\": false,\n" +
                                "  \"keyId\": \"INVALID-KEY\",\n" +
                                "  \"error\": \"Key not found or expired\"\n" +
                                "}")));

        // Default key validation for any other key
        server.stubFor(post(urlEqualTo("/api/v1/keyValidation"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"valid\": true,\n" +
                                "  \"status\": \"ACTIVE\",\n" +
                                "  \"permissions\": [\"UNLOCK\", \"LOCK\"]\n" +
                                "}")));
    }

    private void setupKtsStubs(WireMockServer server) {
        // Register Key endpoint - Success case
        server.stubFor(post(urlEqualTo("/api/v1/registerKey"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": true,\n" +
                                "  \"registrationId\": \"REG-12345\",\n" +
                                "  \"keyId\": \"${json-unit.any-string}\",\n" +
                                "  \"status\": \"REGISTERED\",\n" +
                                "  \"timestamp\": \"2024-01-01T12:00:00Z\"\n" +
                                "}")));

        // Update Key Status endpoint - Success case
        server.stubFor(post(urlEqualTo("/api/v1/updateKeyStatus"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": true,\n" +
                                "  \"keyId\": \"${json-unit.any-string}\",\n" +
                                "  \"previousStatus\": \"ACTIVE\",\n" +
                                "  \"newStatus\": \"SUSPENDED\",\n" +
                                "  \"timestamp\": \"2024-01-01T12:00:00Z\"\n" +
                                "}")));

        // Error case for invalid key
        server.stubFor(post(urlEqualTo("/api/v1/updateKeyStatus"))
                .withRequestBody(containing("\"keyId\":\"INVALID-KEY\""))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": false,\n" +
                                "  \"error\": \"Key not found\",\n" +
                                "  \"keyId\": \"INVALID-KEY\"\n" +
                                "}")));
    }

    private void setupTelematicsStubs(WireMockServer server) {
        // Send Command endpoint - Success case
        server.stubFor(post(urlEqualTo("/api/v1/sendCommand"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": true,\n" +
                                "  \"commandId\": \"CMD-12345\",\n" +
                                "  \"vehicleId\": \"${json-unit.any-string}\",\n" +
                                "  \"command\": \"UNLOCK\",\n" +
                                "  \"status\": \"EXECUTED\",\n" +
                                "  \"timestamp\": \"2024-01-01T12:00:00Z\"\n" +
                                "}")));

        // Provision Pairing Verifier endpoint
        server.stubFor(post(urlEqualTo("/api/v1/provisionPairingVerifier"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": true,\n" +
                                "  \"verifierId\": \"VERIFIER-12345\",\n" +
                                "  \"vehicleId\": \"${json-unit.any-string}\",\n" +
                                "  \"status\": \"PROVISIONED\",\n" +
                                "  \"expirationTime\": \"2024-01-01T13:00:00Z\"\n" +
                                "}")));

        // Error case for vehicle not found
        server.stubFor(post(urlEqualTo("/api/v1/sendCommand"))
                .withRequestBody(containing("\"vehicleId\":\"INVALID-VIN\""))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"success\": false,\n" +
                                "  \"error\": \"Vehicle not found\",\n" +
                                "  \"vehicleId\": \"INVALID-VIN\"\n" +
                                "}")));
    }
}
