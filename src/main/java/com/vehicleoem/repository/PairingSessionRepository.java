package com.vehicleoem.repository;

import com.vehicleoem.model.PairingSession;
import com.vehicleoem.model.PairingStatus;
import com.vehicleoem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PairingSessionRepository extends JpaRepository<PairingSession, Long> {
    Optional<PairingSession> findBySessionId(String sessionId);
    Optional<PairingSession> findByVehicleAndStatus(Vehicle vehicle, PairingStatus status);
    List<PairingSession> findByVehicle(Vehicle vehicle);
    List<PairingSession> findByStatus(PairingStatus status);
    List<PairingSession> findByInitiatedBy(String initiatedBy);
    
    @Query("SELECT p FROM PairingSession p WHERE p.status = 'INITIATED' AND p.expiresAt < :date")
    List<PairingSession> findExpiredSessions(@Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM PairingSession p WHERE p.vehicle.vin = :vin ORDER BY p.createdAt DESC")
    List<PairingSession> findByVehicleVinOrderByCreatedAtDesc(@Param("vin") String vin);
    
    @Query("SELECT COUNT(p) FROM PairingSession p WHERE p.vehicle = :vehicle AND p.status = 'COMPLETED'")
    Long countCompletedPairingsByVehicle(@Param("vehicle") Vehicle vehicle);
}
