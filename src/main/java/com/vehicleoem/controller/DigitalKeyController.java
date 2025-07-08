package com.vehicleoem.controller;

import com.vehicleoem.api.DigitalKeyApi;
import com.vehicleoem.dto.TrackKeyRequest;
import com.vehicleoem.dto.TrackKeyResponse;
import com.vehicleoem.dto.ManageKeyRequest;
import com.vehicleoem.dto.ManageKeyResponse;
import com.vehicleoem.service.DigitalKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/keys")
public class DigitalKeyController implements DigitalKeyApi {
    
    @Autowired
    private DigitalKeyService digitalKeyService;
    
    @Override
    public ResponseEntity<TrackKeyResponse> trackKey(@Valid @RequestBody TrackKeyRequest request) {
        TrackKeyResponse response = digitalKeyService.trackKey(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @Override
    public ResponseEntity<ManageKeyResponse> manageKey(@Valid @RequestBody ManageKeyRequest request) {
        ManageKeyResponse response = digitalKeyService.manageKey(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
