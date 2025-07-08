package com.vehicleoem.client;

import com.vehicleoem.dto.TelematicsCommandRequest;
import com.vehicleoem.dto.TelematicsResponse;
import com.vehicleoem.dto.PairingVerifierRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "vehicle-telematics", url = "${feign.vehicle-telematics.url}")
public interface VehicleTelematicsClient {
    
    @PostMapping("/api/v1/sendCommand")
    TelematicsResponse sendCommand(@RequestBody TelematicsCommandRequest request);
    
    @PostMapping("/api/v1/provisionPairingVerifier")
    TelematicsResponse provisionPairingVerifier(@RequestBody PairingVerifierRequest request);
}
