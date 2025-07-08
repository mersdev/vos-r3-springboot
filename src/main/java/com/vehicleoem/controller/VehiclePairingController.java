package com.vehicleoem.controller;

import com.vehicleoem.api.VehiclePairingApi;
import com.vehicleoem.dto.*;
import com.vehicleoem.model.PairingSession;
import com.vehicleoem.service.VehiclePairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pairing")
public class VehiclePairingController implements VehiclePairingApi {
    
    @Autowired
    private VehiclePairingService vehiclePairingService;
    
    @PostMapping("/initiate")
    @Override
    public ResponseEntity<InitiatePairingResponse> initiatePairing(@Valid @RequestBody InitiatePairingRequest request) {
        try {
            InitiatePairingResponse response = vehiclePairingService.initiatePairing(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new InitiatePairingResponse(false, e.getMessage(), null, null, null));
        }
    }
    
    @PostMapping("/complete")
    @Override
    public ResponseEntity<CompletePairingResponse> completePairing(@Valid @RequestBody CompletePairingRequest request) {
        try {
            CompletePairingResponse response = vehiclePairingService.completePairing(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new CompletePairingResponse(false, e.getMessage(), null, null));
        }
    }
    
    @PostMapping("/revoke/{sessionId}")
    @Override
    public ResponseEntity<String> revokePairingSession(
            @PathVariable String sessionId,
            @RequestParam String revokedBy,
            @RequestParam(required = false) String reason) {
        try {
            vehiclePairingService.revokePairingSession(sessionId, revokedBy, reason);
            return ResponseEntity.ok("Pairing session revoked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to revoke pairing session: " + e.getMessage());
        }
    }
}
