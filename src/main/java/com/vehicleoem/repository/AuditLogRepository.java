package com.vehicleoem.repository;

import com.vehicleoem.model.AuditLog;
import com.vehicleoem.model.AuditSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<AuditLog> findByPerformedBy(String performedBy);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findBySeverity(AuditSeverity severity);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentAuditLogs(@Param("entityType") String entityType, @Param("since") LocalDateTime since);
    
    @Query("SELECT a FROM AuditLog a WHERE a.performedBy = :user AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditLog> findUserActivity(@Param("user") String user, @Param("since") LocalDateTime since);
}
