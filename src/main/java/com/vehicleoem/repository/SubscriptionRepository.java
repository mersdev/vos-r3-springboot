package com.vehicleoem.repository;

import com.vehicleoem.model.Subscription;
import com.vehicleoem.model.SubscriptionStatus;
import com.vehicleoem.model.SubscriptionTier;
import com.vehicleoem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByVehicle(Vehicle vehicle);
    List<Subscription> findByStatus(SubscriptionStatus status);
    List<Subscription> findByTier(SubscriptionTier tier);
    List<Subscription> findByNextBillingDateBefore(LocalDateTime date);
    List<Subscription> findByExpiresAtBefore(LocalDateTime date);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'GRACE_PERIOD' AND s.gracePeriodEndsAt < :date")
    List<Subscription> findExpiredSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.nextBillingDate BETWEEN :start AND :end")
    List<Subscription> findSubscriptionsForRenewal(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
    Long countByStatus(@Param("status") SubscriptionStatus status);
    
    @Query("SELECT s.tier, COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' GROUP BY s.tier")
    List<Object[]> getActiveSubscriptionsByTier();
}
