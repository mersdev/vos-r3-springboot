package com.vehicleoem.repository;

import com.vehicleoem.model.KeySharingInvitation;
import com.vehicleoem.model.InvitationStatus;
import com.vehicleoem.model.Vehicle;
import com.vehicleoem.model.DigitalKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KeySharingInvitationRepository extends JpaRepository<KeySharingInvitation, Long> {
    Optional<KeySharingInvitation> findByInvitationCode(String invitationCode);
    Optional<KeySharingInvitation> findByDigitalKey(DigitalKey digitalKey);
    List<KeySharingInvitation> findByVehicle(Vehicle vehicle);
    List<KeySharingInvitation> findByVehicleAndStatus(Vehicle vehicle, InvitationStatus status);
    List<KeySharingInvitation> findByFriendEmail(String friendEmail);
    List<KeySharingInvitation> findByStatus(InvitationStatus status);
    List<KeySharingInvitation> findBySharedBy(String sharedBy);
    
    boolean existsByVehicleAndFriendEmailAndStatus(Vehicle vehicle, String friendEmail, InvitationStatus status);
    
    @Query("SELECT i FROM KeySharingInvitation i WHERE i.status = 'PENDING' AND i.invitationExpiresAt < :date")
    List<KeySharingInvitation> findExpiredInvitations(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM KeySharingInvitation i WHERE i.vehicle.owner.accountId = :ownerId")
    List<KeySharingInvitation> findByOwnerAccountId(@Param("ownerId") String ownerId);
    
    @Query("SELECT COUNT(i) FROM KeySharingInvitation i WHERE i.vehicle = :vehicle AND i.status = 'ACCEPTED'")
    Long countAcceptedInvitationsByVehicle(@Param("vehicle") Vehicle vehicle);
}
