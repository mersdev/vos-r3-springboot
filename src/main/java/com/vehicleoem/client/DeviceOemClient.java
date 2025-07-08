package com.vehicleoem.client;

import com.vehicleoem.dto.EventNotificationRequest;
import com.vehicleoem.dto.KeyValidationRequest;
import com.vehicleoem.dto.KeyValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "device-oem-server", url = "${feign.device-oem-server.url}")
public interface DeviceOemClient {
    
    @PostMapping("/api/v1/eventNotification")
    void sendEventNotification(@RequestBody EventNotificationRequest request);
    
    @PostMapping("/api/v1/keyValidation")
    KeyValidationResponse validateKey(@RequestBody KeyValidationRequest request);
}
