package com.vehicleoem.repository;

import com.vehicleoem.model.DigitalKey;
import com.vehicleoem.model.Vehicle;
import com.vehicleoem.model.KeyStatus;
import com.vehicleoem.model.KeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DigitalKeyRepository extends JpaRepository<DigitalKey, Long> {
    Optional<DigitalKey> findByKeyId(String keyId);
    List<DigitalKey> findByVehicle(Vehicle vehicle);
    List<DigitalKey> findByDeviceId(String deviceId);
    List<DigitalKey> findByStatus(KeyStatus status);
    List<DigitalKey> findByVehicleVin(String vin);
    List<DigitalKey> findByVehicleVinAndKeyType(String vin, KeyType keyType);
    List<DigitalKey> findByKeyType(KeyType keyType);
    boolean existsByKeyId(String keyId);
}
