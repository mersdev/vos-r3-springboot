package com.vehicleoem.service;

import com.vehicleoem.model.DigitalKey;
import com.vehicleoem.client.DeviceOemClient;
import com.vehicleoem.dto.EventNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventNotificationService {
    
    @Autowired
    private DeviceOemClient deviceOemClient;
    
    @Async
    @CircuitBreaker(name = "eventNotification")
    @Retry(name = "eventNotification")
    public void sendKeyTrackedNotification(DigitalKey digitalKey) {
        try {
            EventNotificationRequest request = new EventNotificationRequest();
            request.setEventType("KEY_TRACKED");
            request.setKeyId(digitalKey.getKeyId());
            request.setDeviceId(digitalKey.getDeviceId());
            request.setVehicleId(digitalKey.getVehicle().getVin());
            request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            deviceOemClient.sendEventNotification(request);
        } catch (Exception e) {
            System.err.println("Failed to send key tracked notification: " + e.getMessage());
        }
    }
    
    @Async
    @CircuitBreaker(name = "eventNotification")
    @Retry(name = "eventNotification")
    public void sendKeyStatusChangedNotification(DigitalKey digitalKey, String action) {
        try {
            EventNotificationRequest request = new EventNotificationRequest();
            request.setEventType("KEY_STATUS_CHANGED");
            request.setKeyId(digitalKey.getKeyId());
            request.setDeviceId(digitalKey.getDeviceId());
            request.setVehicleId(digitalKey.getVehicle().getVin());
            request.setAction(action);
            request.setNewStatus(digitalKey.getStatus().name());
            request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            deviceOemClient.sendEventNotification(request);
        } catch (Exception e) {
            System.err.println("Failed to send key status changed notification: " + e.getMessage());
        }
    }
}
