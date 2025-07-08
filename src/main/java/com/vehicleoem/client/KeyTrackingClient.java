package com.vehicleoem.client;

import com.vehicleoem.dto.KeyRegistrationRequest;
import com.vehicleoem.dto.KeyRegistrationResponse;
import com.vehicleoem.dto.KeyStatusUpdateRequest;
import com.vehicleoem.dto.KeyStatusUpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kts-server", url = "${feign.kts-server.url}")
public interface KeyTrackingClient {
    
    @PostMapping("/api/v1/registerKey")
    KeyRegistrationResponse registerKey(@RequestBody KeyRegistrationRequest request);
    
    @PostMapping("/api/v1/updateKeyStatus")
    KeyStatusUpdateResponse updateKeyStatus(@RequestBody KeyStatusUpdateRequest request);
}
