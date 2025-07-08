package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "entity_type", length = 50)
    private String entityType; // DIGITAL_KEY, VEHICLE, OWNER_ACCOUNT
    
    @NotBlank
    @Column(name = "entity_id", length = 100)
    private String entityId;
    
    @NotBlank
    @Column(name = "action", length = 50)
    private String action; // CREATE, UPDATE, DELETE, SUSPEND, RESUME, etc.
    
    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON format
    
    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON format
    
    @Column(name = "performed_by", length = 100)
    private String performedBy;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    @Column(name = "reason", length = 255)
    private String reason;
    
    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private AuditSeverity severity = AuditSeverity.INFO;
    
    // Constructors
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AuditLog(String entityType, String entityId, String action, String performedBy) {
        this();
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.performedBy = performedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    
    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }
    
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public AuditSeverity getSeverity() { return severity; }
    public void setSeverity(AuditSeverity severity) { this.severity = severity; }
}
