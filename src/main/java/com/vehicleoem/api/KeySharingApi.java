package com.vehicleoem.api;

import com.vehicleoem.dto.ShareKeyRequest;
import com.vehicleoem.dto.ShareKeyResponse;
import com.vehicleoem.model.*;
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
import java.util.List;

@Tag(name = "Key Sharing Management", description = "APIs for sharing digital keys with friends and managing permissions")
public interface KeySharingApi {

    @Operation(
        summary = "Share Digital Key",
        description = """
            Shares a digital key with a friend by creating an invitation. The friend will receive
            an invitation code that can be used to accept the shared key.
            
            **Features:**
            - Creates secure invitation with expiration
            - Validates vehicle ownership and subscription tier
            - Sends notification to friend
            - Supports permission levels and restrictions
            
            **Permission Levels:**
            - **UNLOCK_ONLY**: Can only unlock the vehicle
            - **DRIVE_ONLY**: Can unlock and start the vehicle
            - **FULL_ACCESS**: Complete vehicle access including settings
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Key sharing request parameters",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Share with Friend - Drive Only",
                        summary = "Share key with drive-only permissions",
                        description = "Example of sharing a key with basic driving permissions",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "friendEmail": "jane.doe@example.com",
                              "friendName": "Jane Doe",
                              "permissionLevel": "DRIVE_ONLY",
                              "expiresAt": "2025-08-01T23:59:59",
                              "sharedBy": "john.doe@example.com",
                              "message": "Sharing my car key with you for the weekend"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Share with Family - Full Access",
                        summary = "Share key with full access permissions",
                        description = "Example of sharing a key with complete vehicle access",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "friendEmail": "spouse@example.com",
                              "friendName": "Alex Smith",
                              "permissionLevel": "FULL_ACCESS",
                              "expiresAt": "2025-12-31T23:59:59",
                              "sharedBy": "john.doe@example.com",
                              "message": "Permanent access for family member"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Temporary Share - Unlock Only",
                        summary = "Temporary key with unlock-only permissions",
                        description = "Example of sharing a key for temporary access",
                        value = """
                            {
                              "vehicleVin": "1HGBH41JXMN109186",
                              "friendEmail": "delivery@example.com",
                              "friendName": "Delivery Service",
                              "permissionLevel": "UNLOCK_ONLY",
                              "expiresAt": "2025-07-08T18:00:00",
                              "sharedBy": "john.doe@example.com",
                              "message": "Access for package delivery"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Key shared successfully",
            content = @Content(schema = @Schema(implementation = ShareKeyResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions or subscription tier")
    })
    ResponseEntity<ShareKeyResponse> shareKey(@Valid @RequestBody ShareKeyRequest request);

    @Operation(
        summary = "Accept Key Invitation",
        description = """
            Accepts a key sharing invitation using the invitation code received via email.
            This creates a new digital key for the accepting device.
            
            **Features:**
            - Validates invitation code and expiration
            - Creates digital key for accepting device
            - Registers key with external services
            - Sends confirmation notifications
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitation accepted successfully",
            content = @Content(schema = @Schema(implementation = DigitalKey.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or expired invitation code"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<DigitalKey> acceptInvitation(
        @Parameter(description = "Invitation code received via email", required = true, example = "INV-1B1C92C4F3CE")
        @PathVariable String invitationCode,
        @Parameter(description = "Device ID of the accepting device", required = true, example = "SAMSUNG-S23-XYZ789")
        @RequestParam String deviceId,
        @Parameter(description = "Device manufacturer", required = true, example = "Samsung")
        @RequestParam String deviceOem);

    @Operation(
        summary = "Revoke Shared Key",
        description = """
            Revokes a previously shared digital key, immediately disabling access.
            This action cannot be undone.
            
            **Features:**
            - Immediately disables key access
            - Notifies all parties
            - Updates external services
            - Creates audit trail
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Key revoked successfully"),
        @ApiResponse(responseCode = "400", description = "Key not found or cannot be revoked"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to revoke key")
    })
    ResponseEntity<String> revokeSharedKey(
        @Parameter(description = "Digital key ID to revoke", required = true, example = "FRIEND-KEY-87654321")
        @PathVariable String keyId,
        @Parameter(description = "Account ID of the person revoking the key", required = true, example = "ACC1234567890")
        @RequestParam String revokedBy,
        @Parameter(description = "Reason for revocation", example = "No longer needed")
        @RequestParam(required = false) String reason);

    @Operation(
        summary = "Update Key Permissions",
        description = """
            Updates the permission level for an existing shared key.
            
            **Permission Levels:**
            - **UNLOCK_ONLY**: Can only unlock the vehicle
            - **DRIVE_ONLY**: Can unlock and start the vehicle  
            - **FULL_ACCESS**: Complete vehicle access including settings
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid permission level or key not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to update key")
    })
    ResponseEntity<String> updateKeyPermissions(
        @Parameter(description = "Digital key ID", required = true, example = "FRIEND-KEY-87654321")
        @PathVariable String keyId,
        @Parameter(description = "New permission level", required = true, example = "FULL_ACCESS")
        @RequestParam PermissionLevel permissionLevel,
        @Parameter(description = "Account ID of the person updating permissions", required = true, example = "ACC1234567890")
        @RequestParam String updatedBy);

    @Operation(
        summary = "Set Key Restrictions",
        description = """
            Sets usage restrictions for a shared digital key.
            
            **Restriction Types:**
            - **Time Restrictions**: Limit usage to specific time periods (e.g., "09:00-17:00")
            - **Location Restrictions**: Limit usage to specific locations (e.g., "Home,Office")
            - **Usage Count**: Maximum number of times the key can be used
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restrictions updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid restrictions or key not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to set restrictions")
    })
    ResponseEntity<String> setKeyRestrictions(
        @Parameter(description = "Digital key ID", required = true, example = "FRIEND-KEY-87654321")
        @PathVariable String keyId,
        @Parameter(description = "Time restrictions (e.g., '09:00-17:00')", example = "09:00-17:00")
        @RequestParam(required = false) String timeRestrictions,
        @Parameter(description = "Location restrictions (comma-separated)", example = "Home,Office")
        @RequestParam(required = false) String locationRestrictions,
        @Parameter(description = "Maximum usage count", example = "10")
        @RequestParam(required = false) Long maxUsageCount,
        @Parameter(description = "Account ID of the person setting restrictions", required = true, example = "ACC1234567890")
        @RequestParam String updatedBy);

    @Operation(
        summary = "Get Pending Invitations",
        description = """
            Retrieves all pending key sharing invitations for a specific vehicle.
            
            **Features:**
            - Lists all active invitations
            - Shows invitation details and expiration
            - Includes invitation status
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Vehicle not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to view invitations")
    })
    ResponseEntity<List<KeySharingInvitation>> getPendingInvitations(
        @Parameter(description = "Vehicle VIN", required = true, example = "1HGBH41JXMN109186")
        @PathVariable String vehicleVin);

    @Operation(
        summary = "Get Shared Keys",
        description = """
            Retrieves all shared digital keys for a specific vehicle.
            
            **Features:**
            - Lists all active shared keys
            - Shows key details and permissions
            - Includes usage statistics
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shared keys retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Vehicle not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to view shared keys")
    })
    ResponseEntity<List<DigitalKey>> getSharedKeys(
        @Parameter(description = "Vehicle VIN", required = true, example = "1HGBH41JXMN109186")
        @PathVariable String vehicleVin);
}
