package com.vehicleoem.api;

import com.vehicleoem.model.OwnerAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Owner Account Management", description = "APIs for managing vehicle owner accounts")
public interface OwnerAccountApi {

    @Operation(
        summary = "Create Owner Account",
        description = """
            Creates a new owner account with the provided details. The account is required
            for vehicle ownership and digital key management.
            
            **Features:**
            - Unique account ID validation
            - Email uniqueness verification
            - Account status initialization
            - Security settings setup
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Owner account creation parameters",
            content = @Content(
                mediaType = "application/x-www-form-urlencoded",
                examples = {
                    @ExampleObject(
                        name = "Personal Account",
                        summary = "Create personal owner account",
                        description = "Example of creating a personal vehicle owner account",
                        value = "accountId=ACC1234567890&email=john.doe@example.com&firstName=John&lastName=Doe"
                    ),
                    @ExampleObject(
                        name = "Business Account",
                        summary = "Create business owner account",
                        description = "Example of creating a business vehicle owner account",
                        value = "accountId=ACC9876543210&email=jane.smith@company.com&firstName=Jane&lastName=Smith"
                    ),
                    @ExampleObject(
                        name = "Test Account",
                        summary = "Create test account",
                        description = "Example test account for development and testing",
                        value = "accountId=ACC1122334455&email=test.user@example.com&firstName=Test&lastName=User"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = OwnerAccount.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or account already exists"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<OwnerAccount> createOwnerAccount(
        @Parameter(description = "Unique account identifier (6-20 alphanumeric uppercase)", required = true, example = "ACC1234567890")
        @RequestParam String accountId,
        @Parameter(description = "Owner email address", required = true, example = "john.doe@example.com")
        @RequestParam String email,
        @Parameter(description = "Owner first name", required = true, example = "John")
        @RequestParam String firstName,
        @Parameter(description = "Owner last name", required = true, example = "Doe")
        @RequestParam String lastName);

    @Operation(
        summary = "Get Owner Account by ID",
        description = """
            Retrieves an owner account by its unique account ID.
            
            **Features:**
            - Account details retrieval
            - Associated vehicles information
            - Account status and verification details
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found",
            content = @Content(schema = @Schema(implementation = OwnerAccount.class))),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<OwnerAccount> getOwnerAccount(
        @Parameter(description = "Account ID", required = true, example = "ACC1234567890")
        @PathVariable String accountId);

    @Operation(
        summary = "Get Owner Account by Email",
        description = """
            Retrieves an owner account by email address.
            
            **Features:**
            - Email-based account lookup
            - Account details retrieval
            - Associated vehicles information
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found",
            content = @Content(schema = @Schema(implementation = OwnerAccount.class))),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<OwnerAccount> getOwnerAccountByEmail(
        @Parameter(description = "Owner email address", required = true, example = "john.doe@example.com")
        @PathVariable String email);

    @Operation(
        summary = "Verify Owner Email",
        description = """
            Verifies the owner's email address, which is required for digital key operations.
            Email verification is a prerequisite for creating and managing digital keys.
            
            **Features:**
            - Email verification status update
            - Enables digital key operations
            - Security compliance
            
            **Note:** This is typically done through an email verification flow in production,
            but this endpoint allows direct verification for testing purposes.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Account not found or verification failed"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<String> verifyEmail(
        @Parameter(description = "Account ID", required = true, example = "ACC1234567890")
        @PathVariable String accountId);
}
