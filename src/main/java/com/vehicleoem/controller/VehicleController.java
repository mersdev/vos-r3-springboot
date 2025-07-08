package com.vehicleoem.controller;

import com.vehicleoem.api.VehicleApi;
import com.vehicleoem.model.Vehicle;
import com.vehicleoem.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController implements VehicleApi {
    
    @Autowired
    private VehicleService vehicleService;
    
    @PostMapping
    @Override
    public ResponseEntity<?> createVehicle(
            @RequestParam String vin,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam String ownerAccountId) {

        try {
            Vehicle vehicle = vehicleService.createVehicle(vin, make, model, year, ownerAccountId);
            return ResponseEntity.ok(vehicle);
        } catch (IllegalArgumentException e) {
            // Return detailed error message for debugging
            return ResponseEntity.badRequest().body(Map.of(
                "error", "INVALID_ARGUMENT",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            // Return detailed error message for debugging
            return ResponseEntity.badRequest().body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }
    
    @PostMapping("/{vin}/initialize-pairing")
    @Override
    public ResponseEntity<String> initializePairing(@PathVariable String vin) {
        try {
            vehicleService.initializePairing(vin);
            return ResponseEntity.ok("Pairing initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to initialize pairing: " + e.getMessage());
        }
    }
    
    @PutMapping("/{vin}/subscription")
    @Override
    public ResponseEntity<String> updateSubscription(
            @PathVariable String vin,
            @RequestParam boolean active,
            @RequestParam(required = false) String expiresAt) {

        try {
            LocalDateTime expirationDate = expiresAt != null ?
                LocalDateTime.parse(expiresAt) : LocalDateTime.now().plusYears(1);

            vehicleService.updateSubscription(vin, active, expirationDate);
            return ResponseEntity.ok("Subscription updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update subscription: " + e.getMessage());
        }
    }

    @PostMapping("/test-friendly")
    @Override
    public ResponseEntity<?> createVehicleTestFriendly(
            @RequestParam String vin,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam String ownerAccountId) {

        try {
            Vehicle vehicle = vehicleService.createVehicleWithRelaxedValidation(vin, make, model, year, ownerAccountId);
            return ResponseEntity.ok(vehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "INVALID_ARGUMENT",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping("/{vin}/subscription-tier")
    @Override
    public ResponseEntity<String> updateSubscriptionTier(
            @PathVariable String vin,
            @RequestParam String tier) {

        try {
            vehicleService.updateSubscriptionTier(vin, tier);
            return ResponseEntity.ok("Subscription tier updated successfully to " + tier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update subscription tier: " + e.getMessage());
        }
    }
}
