package com.vehicleoem.api;

import com.vehicleoem.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vehicle Pairing Management", description = "APIs for managing vehicle-device pairing processes")
public interface VehiclePairingApi {

    @Operation(
        summary = "Initiate Vehicle Pairing",
        description = """
            Initiates the pairing process between a vehicle and a device. This creates a secure
            pairing session and generates the necessary cryptographic credentials.
            
            **Features:**
            - Creates secure pairing session
            - Generates ephemeral key pairs
            - Sends pairing credentials to vehicle
            - Validates vehicle and owner eligibility
            
            **Pairing Process:**
            1. Validate vehicle and owner account
            2. Generate pairing password and verifier
            3. Create ephemeral key pair for secure communication
            4. Send credentials to vehicle via telematics
            5. Return session details for completion
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Vehicle pairing initiation request",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "iPhone Pairing",
                        summary = "Initiate pairing with iPhone",
                        description = "Example of initiating pairing with an iPhone device",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "deviceId": "IPHONE-14-ABC123",
                              "deviceOem": "Apple",
                              "ownerAccountId": "ACC1234567890",
                              "initiatedBy": "john.doe@example.com"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Android Pairing",
                        summary = "Initiate pairing with Android device",
                        description = "Example of initiating pairing with an Android device",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "deviceId": "SAMSUNG-S23-XYZ789",
                              "deviceOem": "Samsung",
                              "ownerAccountId": "ACC1234567890",
                              "initiatedBy": "jane.smith@example.com"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Test Device Pairing",
                        summary = "Initiate pairing with test device",
                        description = "Example for testing and development purposes",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "deviceId": "TEST-DEVICE-001",
                              "deviceOem": "TestOEM",
                              "ownerAccountId": "ACC1234567890",
                              "initiatedBy": "test.user@example.com"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pairing initiated successfully",
            content = @Content(schema = @Schema(implementation = InitiatePairingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or vehicle not eligible for pairing"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions or account not verified"),
        @ApiResponse(responseCode = "409", description = "Vehicle already has an active pairing session")
    })
    ResponseEntity<InitiatePairingResponse> initiatePairing(@Valid @RequestBody InitiatePairingRequest request);

    @Operation(
        summary = "Complete Vehicle Pairing",
        description = """
            Completes the vehicle pairing process by validating the pairing credentials
            and establishing the secure connection between device and vehicle.
            
            **Features:**
            - Validates pairing password and session
            - Verifies device public key and certificate
            - Creates secure communication channel
            - Registers device with vehicle
            
            **Completion Process:**
            1. Validate session ID and pairing password
            2. Verify device public key and certificate
            3. Establish secure communication channel
            4. Register device as paired with vehicle
            5. Create initial owner digital key
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Vehicle pairing completion request",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Complete iPhone Pairing",
                        summary = "Complete pairing with iPhone",
                        description = "Example of completing pairing with an iPhone device",
                        value = """
                            {
                              "sessionId": "PAIR-SESSION-12345",
                              "pairingPassword": "123456",
                              "devicePublicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
                              "deviceCertificate": "MIIBkTCCATegAwIBAgIJAL...",
                              "completedBy": "john.doe@example.com"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Complete Android Pairing",
                        summary = "Complete pairing with Android device",
                        description = "Example of completing pairing with an Android device",
                        value = """
                            {
                              "sessionId": "PAIR-SESSION-67890",
                              "pairingPassword": "654321",
                              "devicePublicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
                              "deviceCertificate": "MIIBkTCCATegAwIBAgIJAL...",
                              "completedBy": "jane.smith@example.com"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pairing completed successfully",
            content = @Content(schema = @Schema(implementation = CompletePairingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pairing credentials or session expired"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Pairing session not found")
    })
    ResponseEntity<CompletePairingResponse> completePairing(@Valid @RequestBody CompletePairingRequest request);

    @Operation(
        summary = "Revoke Pairing Session",
        description = """
            Revokes an active pairing session, terminating the pairing process.
            This can be used to cancel ongoing pairing or remove completed pairings.
            
            **Features:**
            - Terminates active pairing session
            - Removes pairing credentials from vehicle
            - Notifies all parties
            - Creates audit trail
            
            **Use Cases:**
            - Cancel ongoing pairing process
            - Remove device access after theft/loss
            - Administrative cleanup
            - Security incident response
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pairing session revoked successfully"),
        @ApiResponse(responseCode = "400", description = "Session not found or cannot be revoked"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to revoke session")
    })
    ResponseEntity<String> revokePairingSession(
        @Parameter(description = "Pairing session ID to revoke", required = true, example = "PAIR-SESSION-12345")
        @PathVariable String sessionId,
        @Parameter(description = "Account ID of the person revoking the session", required = true, example = "ACC1234567890")
        @RequestParam String revokedBy,
        @Parameter(description = "Reason for revocation", example = "Device lost or stolen")
        @RequestParam(required = false) String reason);
}
