package com.vehicleoem.service;

import com.vehicleoem.model.Vehicle;
import com.vehicleoem.model.OwnerAccount;
import com.vehicleoem.model.SubscriptionTier;
import com.vehicleoem.repository.VehicleRepository;
import com.vehicleoem.repository.OwnerAccountRepository;
import com.vehicleoem.security.CryptographyService;
import com.vehicleoem.security.CertificateService;
import com.vehicleoem.client.VehicleTelematicsClient;
import com.vehicleoem.dto.PairingVerifierRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private OwnerAccountRepository ownerAccountRepository;
    
    @Autowired
    private CryptographyService cryptographyService;
    
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private VehicleTelematicsClient vehicleTelematicsClient;
    
    @Transactional
    public Vehicle createVehicle(String vin, String make, String model, Integer year, String ownerAccountId) {
        // Find owner
        OwnerAccount owner = ownerAccountRepository.findByAccountId(ownerAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Owner account not found: " + ownerAccountId));
        
        // Check if vehicle already exists
        if (vehicleRepository.existsByVin(vin)) {
            throw new IllegalArgumentException("Vehicle already exists: " + vin);
        }
        
        // Create vehicle
        Vehicle vehicle = new Vehicle(vin, make, model, year, owner);
        
        // Generate and set public key certificate
        String publicKeyCertificate = certificateService.generateVehiclePublicKeyCertificate(vin);
        vehicle.setPublicKeyCertificate(publicKeyCertificate);
        
        // Set subscription active by default
        vehicle.setSubscriptionActive(true);
        vehicle.setSubscriptionExpiresAt(LocalDateTime.now().plusYears(1));
        
        return vehicleRepository.save(vehicle);
    }
    
    @Transactional
    public void initializePairing(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vin));
        
        // Generate pairing password and verifier
        String pairingPassword = cryptographyService.generatePairingPassword();
        String pairingVerifier = cryptographyService.generatePairingVerifier(pairingPassword);
        
        vehicle.setPairingPassword(pairingPassword);
        vehicle.setPairingVerifier(pairingVerifier);
        
        vehicleRepository.save(vehicle);
        
        // Send pairing verifier to vehicle via telematics
        provisionPairingVerifier(vehicle, pairingVerifier);
    }
    
    private void provisionPairingVerifier(Vehicle vehicle, String verifier) {
        try {
            PairingVerifierRequest request = new PairingVerifierRequest();
            request.setVehicleId(vehicle.getVin());
            request.setVerifier(verifier);
            request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            vehicleTelematicsClient.provisionPairingVerifier(request);
        } catch (Exception e) {
            System.err.println("Failed to provision pairing verifier: " + e.getMessage());
        }
    }
    
    @Transactional
    public void updateSubscription(String vin, boolean active, LocalDateTime expiresAt) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vin));

        vehicle.setSubscriptionActive(active);
        vehicle.setSubscriptionExpiresAt(expiresAt);

        vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle createVehicleWithRelaxedValidation(String vin, String make, String model, Integer year, String ownerAccountId) {
        // Find owner
        OwnerAccount owner = ownerAccountRepository.findByAccountId(ownerAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Owner account not found: " + ownerAccountId));

        // Check if vehicle already exists
        if (vehicleRepository.existsByVin(vin)) {
            throw new IllegalArgumentException("Vehicle already exists: " + vin);
        }

        // Create vehicle without validation
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vin);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setOwner(owner);

        // Generate and set public key certificate
        String publicKeyCertificate = certificateService.generateVehiclePublicKeyCertificate(vin);
        vehicle.setPublicKeyCertificate(publicKeyCertificate);

        // Set subscription active by default
        vehicle.setSubscriptionActive(true);
        vehicle.setSubscriptionExpiresAt(LocalDateTime.now().plusYears(1));

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void updateSubscriptionTier(String vin, String tierName) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vin));

        SubscriptionTier tier;
        try {
            tier = SubscriptionTier.valueOf(tierName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid subscription tier: " + tierName + ". Valid values are: BASIC, PREMIUM, ENTERPRISE");
        }

        vehicle.setSubscriptionTier(tier);
        vehicle.setMaxKeysAllowed(tier.getMaxKeys());
        vehicleRepository.save(vehicle);
    }
}
