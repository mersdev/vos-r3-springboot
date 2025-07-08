package com.vehicleoem.controller;

import com.vehicleoem.api.OwnerAccountApi;
import com.vehicleoem.model.OwnerAccount;
import com.vehicleoem.service.OwnerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class OwnerAccountController implements OwnerAccountApi {
    
    @Autowired
    private OwnerAccountService ownerAccountService;
    
    @PostMapping
    @Override
    public ResponseEntity<OwnerAccount> createOwnerAccount(
            @RequestParam String accountId,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName) {
        
        try {
            OwnerAccount account = ownerAccountService.createOwnerAccount(accountId, email, firstName, lastName);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{accountId}")
    @Override
    public ResponseEntity<OwnerAccount> getOwnerAccount(@PathVariable String accountId) {
        try {
            OwnerAccount account = ownerAccountService.findByAccountId(accountId);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-email/{email}")
    @Override
    public ResponseEntity<OwnerAccount> getOwnerAccountByEmail(@PathVariable String email) {
        try {
            OwnerAccount account = ownerAccountService.findByEmail(email);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{accountId}/verify-email")
    @Override
    public ResponseEntity<String> verifyEmail(@PathVariable String accountId) {
        try {
            ownerAccountService.verifyEmail(accountId);
            return ResponseEntity.ok("Email verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to verify email: " + e.getMessage());
        }
    }
}
