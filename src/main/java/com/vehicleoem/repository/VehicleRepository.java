package com.vehicleoem.repository;

import com.vehicleoem.model.Vehicle;
import com.vehicleoem.model.OwnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);
    List<Vehicle> findByOwner(OwnerAccount owner);
    List<Vehicle> findByOwnerAccountId(String accountId);
    boolean existsByVin(String vin);
}
