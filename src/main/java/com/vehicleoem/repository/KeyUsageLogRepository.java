package com.vehicleoem.repository;

import com.vehicleoem.model.KeyUsageLog;
import com.vehicleoem.model.KeyUsageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KeyUsageLogRepository extends JpaRepository<KeyUsageLog, Long> {
    List<KeyUsageLog> findByKeyId(String keyId);
    List<KeyUsageLog> findByVehicleVin(String vehicleVin);
    List<KeyUsageLog> findByDeviceId(String deviceId);
    List<KeyUsageLog> findByUsageType(KeyUsageType usageType);
    List<KeyUsageLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<KeyUsageLog> findByKeyIdAndTimestampBetween(String keyId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(u) FROM KeyUsageLog u WHERE u.keyId = :keyId AND u.timestamp >= :since")
    Long countUsageByKeyIdSince(@Param("keyId") String keyId, @Param("since") LocalDateTime since);
    
    @Query("SELECT u FROM KeyUsageLog u WHERE u.vehicleVin = :vin AND u.timestamp >= :since ORDER BY u.timestamp DESC")
    List<KeyUsageLog> findRecentUsageByVehicle(@Param("vin") String vin, @Param("since") LocalDateTime since);
    
    @Query("SELECT u.usageType, COUNT(u) FROM KeyUsageLog u WHERE u.keyId = :keyId GROUP BY u.usageType")
    List<Object[]> getUsageStatsByKeyId(@Param("keyId") String keyId);
    
    @Query("SELECT AVG(u.sessionDurationMinutes) FROM KeyUsageLog u WHERE u.keyId = :keyId AND u.sessionDurationMinutes IS NOT NULL")
    Double getAverageSessionDuration(@Param("keyId") String keyId);
}
