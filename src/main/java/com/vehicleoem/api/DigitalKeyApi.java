package com.vehicleoem.api;

import com.vehicleoem.dto.ManageKeyRequest;
import com.vehicleoem.dto.ManageKeyResponse;
import com.vehicleoem.dto.TrackKeyRequest;
import com.vehicleoem.dto.TrackKeyResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Digital Key Management", description = "APIs for managing digital vehicle keys lifecycle")
public interface DigitalKeyApi {

    @Operation(
        summary = "Track Digital Key",
        description = """
            Creates and tracks a new digital key for a vehicle. This endpoint handles both owner keys 
            and friend keys, performing comprehensive validation and registration with external services.
            
            **Key Features:**
            - Validates vehicle ownership and subscription status
            - Registers key with Key Tracking Service (KTS)
            - Sends notifications to Device OEM
            - Supports both owner and friend key types
            - Handles key expiration and usage limits
            
            **Business Rules:**
            - Vehicle must have active subscription
            - Owner account must be verified
            - Friend keys require valid email address
            - Key ID must be unique across the system
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Digital key tracking request with vehicle and device information",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrackKeyRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Owner Key Example",
                        summary = "Track an owner digital key",
                        description = "Example of tracking a new owner key for a vehicle",
                        value = """
                            {
                              "keyId": "OWNER-KEY-12345678",
                              "deviceId": "IPHONE-14-ABC123",
                              "deviceOem": "Apple",
                              "vehicleId": "1HGBH41JXMN109186",
                              "keyType": "OWNER",
                              "publicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
                              "uiBundle": "com.apple.carkey.ui.bundle",
                              "vehicleMobilizationData": "encrypted_mobilization_data_here",
                              "expiresAt": "2025-12-31T23:59:59"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Friend Key Example", 
                        summary = "Track a friend digital key",
                        description = "Example of tracking a friend key with email and restrictions",
                        value = """
                            {
                              "keyId": "FRIEND-KEY-87654321",
                              "deviceId": "SAMSUNG-S23-XYZ789",
                              "deviceOem": "Samsung",
                              "vehicleId": "1HGBH41JXMN109186",
                              "keyType": "FRIEND",
                              "friendEmail": "friend@example.com",
                              "publicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
                              "expiresAt": "2025-06-30T23:59:59"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Digital key tracked successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrackKeyResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "Digital key tracked successfully",
                          "keyId": "OWNER-KEY-12345678",
                          "trackingId": "TRK-2025-001234"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or business rule violation",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Validation Error",
                        value = """
                            {
                              "success": false,
                              "message": "Key ID must be between 10 and 100 characters",
                              "keyId": "SHORT",
                              "trackingId": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Business Rule Error",
                        value = """
                            {
                              "success": false,
                              "message": "Vehicle subscription has expired",
                              "keyId": "OWNER-KEY-12345678",
                              "trackingId": null
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/track")
    ResponseEntity<TrackKeyResponse> trackKey(
        @Parameter(description = "Digital key tracking request", required = true)
        @Valid @RequestBody TrackKeyRequest request);

    @Operation(
        summary = "Manage Digital Key Lifecycle",
        description = """
            Manages the lifecycle of existing digital keys by performing actions such as suspend,
            resume, terminate, or expire. This endpoint provides comprehensive key state management
            with proper validation and audit logging.

            **Supported Actions:**
            - **SUSPEND**: Temporarily disable a key (can be resumed later)
            - **RESUME**: Reactivate a previously suspended key
            - **TERMINATE**: Permanently disable a key (cannot be undone)
            - **EXPIRE**: Mark a key as expired (automatic cleanup)

            **Business Rules:**
            - Only active keys can be suspended
            - Only suspended keys can be resumed
            - Terminated keys cannot be modified
            - Expired keys cannot be resumed
            - All actions require proper authorization
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Key management request with action and metadata",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ManageKeyRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Suspend Key",
                        summary = "Suspend an active digital key",
                        description = "Temporarily disable a key due to security concerns",
                        value = """
                            {
                              "keyId": "OWNER-KEY-12345678",
                              "action": "SUSPEND",
                              "reason": "Suspicious activity detected",
                              "requestedBy": "security-admin"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Resume Key",
                        summary = "Resume a suspended digital key",
                        description = "Reactivate a previously suspended key",
                        value = """
                            {
                              "keyId": "OWNER-KEY-12345678",
                              "action": "RESUME",
                              "reason": "Security investigation completed",
                              "requestedBy": "security-admin"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Terminate Key",
                        summary = "Permanently terminate a digital key",
                        description = "Permanently disable a key (irreversible action)",
                        value = """
                            {
                              "keyId": "FRIEND-KEY-87654321",
                              "action": "TERMINATE",
                              "reason": "Friend access revoked by owner",
                              "requestedBy": "vehicle-owner"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Key management action completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ManageKeyResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Suspend Success",
                        value = """
                            {
                              "success": true,
                              "message": "Digital key suspended successfully",
                              "keyId": "OWNER-KEY-12345678",
                              "newStatus": "SUSPENDED"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Terminate Success",
                        value = """
                            {
                              "success": true,
                              "message": "Digital key terminated successfully",
                              "keyId": "FRIEND-KEY-87654321",
                              "newStatus": "TERMINATED"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or business rule violation"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/manage")
    ResponseEntity<ManageKeyResponse> manageKey(
        @Parameter(description = "Key management request", required = true)
        @Valid @RequestBody ManageKeyRequest request);
}
