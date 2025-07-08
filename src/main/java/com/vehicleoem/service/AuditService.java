package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.repository.AuditLogRepository;
import com.vehicleoem.repository.KeyUsageLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private KeyUsageLogRepository keyUsageLogRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Async
    public void logKeyAction(String keyId, String action, String performedBy, String reason) {
        logAudit("DIGITAL_KEY", keyId, action, performedBy, reason, AuditSeverity.INFO);
    }
    
    @Async
    public void logVehicleAction(String vin, String action, String performedBy, String reason) {
        logAudit("VEHICLE", vin, action, performedBy, reason, AuditSeverity.INFO);
    }
    
    @Async
    public void logOwnerAccountAction(String accountId, String action, String performedBy, String reason) {
        logAudit("OWNER_ACCOUNT", accountId, action, performedBy, reason, AuditSeverity.INFO);
    }
    
    @Async
    public void logSecurityEvent(String entityType, String entityId, String action, String performedBy, String reason) {
        logAudit(entityType, entityId, action, performedBy, reason, AuditSeverity.WARNING);
    }
    
    @Async
    public void logCriticalEvent(String entityType, String entityId, String action, String performedBy, String reason) {
        logAudit(entityType, entityId, action, performedBy, reason, AuditSeverity.CRITICAL);
    }
    
    private void logAudit(String entityType, String entityId, String action, String performedBy, String reason, AuditSeverity severity) {
        try {
            AuditLog auditLog = new AuditLog(entityType, entityId, action, performedBy);
            auditLog.setReason(reason);
            auditLog.setSeverity(severity);
            
            // In a real application, you would capture these from the HTTP request
            // auditLog.setIpAddress(getCurrentIpAddress());
            // auditLog.setUserAgent(getCurrentUserAgent());
            // auditLog.setSessionId(getCurrentSessionId());
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log the error but don't fail the main operation
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
    
    @Async
    public void logKeyUsage(String keyId, String vehicleVin, String deviceId, KeyUsageType usageType) {
        logKeyUsage(keyId, vehicleVin, deviceId, usageType, true, null);
    }
    
    @Async
    public void logKeyUsage(String keyId, String vehicleVin, String deviceId, KeyUsageType usageType, boolean success, String errorMessage) {
        try {
            KeyUsageLog usageLog = new KeyUsageLog(keyId, vehicleVin, deviceId, usageType);
            usageLog.setSuccess(success);
            usageLog.setErrorMessage(errorMessage);
            
            keyUsageLogRepository.save(usageLog);
        } catch (Exception e) {
            System.err.println("Failed to create key usage log: " + e.getMessage());
        }
    }
    
    @Async
    public void logDetailedKeyUsage(String keyId, String vehicleVin, String deviceId, KeyUsageType usageType,
                                   Double latitude, Double longitude, String address,
                                   Integer sessionDuration, Double distance, Double maxSpeed) {
        try {
            KeyUsageLog usageLog = new KeyUsageLog(keyId, vehicleVin, deviceId, usageType);
            usageLog.setLocationLatitude(latitude);
            usageLog.setLocationLongitude(longitude);
            usageLog.setLocationAddress(address);
            usageLog.setSessionDurationMinutes(sessionDuration);
            usageLog.setDistanceTraveledKm(distance);
            usageLog.setMaxSpeedKmh(maxSpeed);
            
            keyUsageLogRepository.save(usageLog);
        } catch (Exception e) {
            System.err.println("Failed to create detailed key usage log: " + e.getMessage());
        }
    }
    
    public List<AuditLog> getAuditHistory(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    public List<AuditLog> getRecentAuditLogs(String entityType, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findRecentAuditLogs(entityType, since);
    }
    
    public List<AuditLog> getUserActivity(String user, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findUserActivity(user, since);
    }
    
    public List<KeyUsageLog> getKeyUsageHistory(String keyId) {
        return keyUsageLogRepository.findByKeyId(keyId);
    }
    
    public List<KeyUsageLog> getVehicleUsageHistory(String vin, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return keyUsageLogRepository.findRecentUsageByVehicle(vin, since);
    }
    
    public Long getKeyUsageCount(String keyId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return keyUsageLogRepository.countUsageByKeyIdSince(keyId, since);
    }
    
    public List<Object[]> getKeyUsageStats(String keyId) {
        return keyUsageLogRepository.getUsageStatsByKeyId(keyId);
    }
    
    public Double getAverageSessionDuration(String keyId) {
        return keyUsageLogRepository.getAverageSessionDuration(keyId);
    }
    
    public List<AuditLog> getSecurityEvents(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findByTimestampBetween(since, LocalDateTime.now())
            .stream()
            .filter(log -> log.getSeverity() == AuditSeverity.WARNING || log.getSeverity() == AuditSeverity.CRITICAL)
            .toList();
    }
}
