package com.vehicleoem.controller;

import com.vehicleoem.api.KeySharingApi;
import com.vehicleoem.dto.ShareKeyRequest;
import com.vehicleoem.dto.ShareKeyResponse;
import com.vehicleoem.model.*;
import com.vehicleoem.service.KeySharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/key-sharing")
public class KeySharingController implements KeySharingApi {
    
    @Autowired
    private KeySharingService keySharingService;
    
    @PostMapping("/share")
    @Override
    public ResponseEntity<ShareKeyResponse> shareKey(@Valid @RequestBody ShareKeyRequest request) {
        try {
            ShareKeyResponse response = keySharingService.shareKey(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ShareKeyResponse(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/accept/{invitationCode}")
    @Override
    public ResponseEntity<DigitalKey> acceptInvitation(
            @PathVariable String invitationCode,
            @RequestParam String deviceId,
            @RequestParam String deviceOem) {
        try {
            DigitalKey digitalKey = keySharingService.acceptKeyInvitation(invitationCode, deviceId, deviceOem);
            return ResponseEntity.ok(digitalKey);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/revoke/{keyId}")
    @Override
    public ResponseEntity<String> revokeSharedKey(
            @PathVariable String keyId,
            @RequestParam String revokedBy,
            @RequestParam(required = false) String reason) {
        try {
            keySharingService.revokeSharedKey(keyId, revokedBy, reason);
            return ResponseEntity.ok("Key revoked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to revoke key: " + e.getMessage());
        }
    }
    
    @PutMapping("/permissions/{keyId}")
    @Override
    public ResponseEntity<String> updateKeyPermissions(
            @PathVariable String keyId,
            @RequestParam PermissionLevel permissionLevel,
            @RequestParam String updatedBy) {
        try {
            keySharingService.updateKeyPermissions(keyId, permissionLevel, updatedBy);
            return ResponseEntity.ok("Permissions updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update permissions: " + e.getMessage());
        }
    }
    
    @PutMapping("/restrictions/{keyId}")
    @Override
    public ResponseEntity<String> setKeyRestrictions(
            @PathVariable String keyId,
            @RequestParam(required = false) String timeRestrictions,
            @RequestParam(required = false) String locationRestrictions,
            @RequestParam(required = false) Long maxUsageCount,
            @RequestParam String updatedBy) {
        try {
            keySharingService.setKeyRestrictions(keyId, timeRestrictions, locationRestrictions, maxUsageCount, updatedBy);
            return ResponseEntity.ok("Restrictions updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update restrictions: " + e.getMessage());
        }
    }
    
    @GetMapping("/invitations/{vehicleVin}")
    @Override
    public ResponseEntity<List<KeySharingInvitation>> getPendingInvitations(@PathVariable String vehicleVin) {
        try {
            List<KeySharingInvitation> invitations = keySharingService.getPendingInvitations(vehicleVin);
            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/shared-keys/{vehicleVin}")
    @Override
    public ResponseEntity<List<DigitalKey>> getSharedKeys(@PathVariable String vehicleVin) {
        try {
            List<DigitalKey> sharedKeys = keySharingService.getSharedKeys(vehicleVin);
            return ResponseEntity.ok(sharedKeys);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
