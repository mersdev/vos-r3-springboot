package com.vehicleoem.api;

import com.vehicleoem.model.Vehicle;
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

@Tag(name = "Vehicle Management", description = "APIs for managing vehicles and their configurations")
public interface VehicleApi {

    @Operation(
        summary = "Create Vehicle",
        description = """
            Creates a new vehicle with standard validation. This endpoint validates VIN format,
            owner account existence, and generates vehicle certificates.
            
            **Features:**
            - VIN format validation (17 characters, excluding I, O, Q)
            - Owner account verification
            - Vehicle certificate generation
            - Subscription tier initialization
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Vehicle creation parameters",
            content = @Content(
                mediaType = "application/x-www-form-urlencoded",
                examples = {
                    @ExampleObject(
                        name = "Honda Accord",
                        summary = "Create a Honda Accord",
                        description = "Example of creating a 2023 Honda Accord with valid VIN",
                        value = "vin=1HGBH41JXMN109186&make=Honda&model=Accord&year=2023&ownerAccountId=ACC1234567890"
                    ),
                    @ExampleObject(
                        name = "Toyota Camry",
                        summary = "Create a Toyota Camry",
                        description = "Example of creating a 2024 Toyota Camry",
                        value = "vin=4T1BF1FK5GU123456&make=Toyota&model=Camry&year=2024&ownerAccountId=ACC9876543210"
                    ),
                    @ExampleObject(
                        name = "BMW X5",
                        summary = "Create a BMW X5",
                        description = "Example of creating a luxury BMW X5",
                        value = "vin=5UXCR6C0XL9123456&make=BMW&model=X5&year=2023&ownerAccountId=ACC1122334455"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle created successfully",
            content = @Content(schema = @Schema(implementation = Vehicle.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<?> createVehicle(
        @Parameter(description = "Vehicle Identification Number (17 characters)", required = true, example = "1HGBH41JXMN109186")
        @RequestParam String vin,
        @Parameter(description = "Vehicle manufacturer", required = true, example = "Honda")
        @RequestParam String make,
        @Parameter(description = "Vehicle model", required = true, example = "Accord")
        @RequestParam String model,
        @Parameter(description = "Vehicle year (1900-2030)", required = true, example = "2023")
        @RequestParam Integer year,
        @Parameter(description = "Owner account ID", required = true, example = "ACC1234567890")
        @RequestParam String ownerAccountId);

    @Operation(
        summary = "Create Vehicle (Test-Friendly)",
        description = """
            Creates a new vehicle with relaxed validation for testing purposes. This endpoint
            bypasses strict VIN validation while maintaining other business logic.
            
            **Features:**
            - Relaxed VIN validation for testing
            - Owner account verification
            - Vehicle certificate generation
            - Subscription tier initialization
            
            **Note:** This endpoint is intended for testing and development environments.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Vehicle creation parameters (test-friendly)",
            content = @Content(
                mediaType = "application/x-www-form-urlencoded",
                examples = {
                    @ExampleObject(
                        name = "Test Vehicle - Short VIN",
                        summary = "Test vehicle with short VIN",
                        description = "Example for testing with non-standard VIN format",
                        value = "vin=TEST123&make=Honda&model=Accord&year=2023&ownerAccountId=ACC1234567890"
                    ),
                    @ExampleObject(
                        name = "Test Vehicle - Custom VIN",
                        summary = "Test vehicle with custom VIN",
                        description = "Example for testing with custom VIN format",
                        value = "vin=CUSTOM-VIN-12345&make=Tesla&model=Model3&year=2024&ownerAccountId=ACC9876543210"
                    ),
                    @ExampleObject(
                        name = "Test Vehicle - Development",
                        summary = "Development test vehicle",
                        description = "Standard test vehicle for development environment",
                        value = "vin=DEV-TEST-001&make=Ford&model=Mustang&year=2023&ownerAccountId=ACC1122334455"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle created successfully",
            content = @Content(schema = @Schema(implementation = Vehicle.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<?> createVehicleTestFriendly(
        @Parameter(description = "Vehicle Identification Number (flexible format for testing)", required = true, example = "TEST123")
        @RequestParam String vin,
        @Parameter(description = "Vehicle manufacturer", required = true, example = "Honda")
        @RequestParam String make,
        @Parameter(description = "Vehicle model", required = true, example = "Accord")
        @RequestParam String model,
        @Parameter(description = "Vehicle year (1900-2030)", required = true, example = "2023")
        @RequestParam Integer year,
        @Parameter(description = "Owner account ID", required = true, example = "ACC1234567890")
        @RequestParam String ownerAccountId);

    @Operation(
        summary = "Initialize Vehicle Pairing",
        description = """
            Initializes the pairing process for a vehicle by generating pairing credentials
            and sending them to the vehicle via telematics.
            
            **Features:**
            - Generates secure pairing password and verifier
            - Sends pairing credentials to vehicle
            - Prepares vehicle for device pairing
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pairing initialized successfully"),
        @ApiResponse(responseCode = "400", description = "Vehicle not found or invalid state"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<String> initializePairing(
        @Parameter(description = "Vehicle Identification Number", required = true, example = "1HGBH41JXMN109186")
        @PathVariable String vin);

    @Operation(
        summary = "Update Vehicle Subscription",
        description = """
            Updates the vehicle's subscription status and expiration date.
            
            **Features:**
            - Enable/disable subscription
            - Set custom expiration date
            - Automatic expiration handling
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
        @ApiResponse(responseCode = "400", description = "Vehicle not found or invalid parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<String> updateSubscription(
        @Parameter(description = "Vehicle Identification Number", required = true, example = "1HGBH41JXMN109186")
        @PathVariable String vin,
        @Parameter(description = "Subscription active status", required = true, example = "true")
        @RequestParam boolean active,
        @Parameter(description = "Subscription expiration date (ISO format)", example = "2025-12-31T23:59:59")
        @RequestParam(required = false) String expiresAt);

    @Operation(
        summary = "Update Vehicle Subscription Tier",
        description = """
            Updates the vehicle's subscription tier (BASIC, PREMIUM, ENTERPRISE).
            This affects the number of allowed keys and available features.
            
            **Subscription Tiers:**
            - **BASIC**: 5 keys, 30-day expiration, owner keys only
            - **PREMIUM**: 20 keys, 90-day expiration, friend keys allowed
            - **ENTERPRISE**: 100 keys, 365-day expiration, all features
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription tier updated successfully"),
        @ApiResponse(responseCode = "400", description = "Vehicle not found or invalid tier"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    ResponseEntity<String> updateSubscriptionTier(
        @Parameter(description = "Vehicle Identification Number", required = true, example = "1HGBH41JXMN109186")
        @PathVariable String vin,
        @Parameter(description = "Subscription tier (BASIC, PREMIUM, ENTERPRISE)", required = true, example = "PREMIUM")
        @RequestParam String tier);
}
