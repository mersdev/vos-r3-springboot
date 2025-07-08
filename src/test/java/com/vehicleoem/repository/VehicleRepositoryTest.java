package com.vehicleoem.repository;

import com.vehicleoem.model.*;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
@DisplayName("VehicleRepository Integration Tests")
class VehicleRepositoryTest {

    private static EmbeddedPostgres embeddedPostgres;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeAll
    static void setUpDatabase() throws IOException {
        embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(0)
                .start();
    }

    @AfterAll
    static void tearDownDatabase() throws IOException {
        if (embeddedPostgres != null) {
            embeddedPostgres.close();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> embeddedPostgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private OwnerAccountRepository ownerAccountRepository;

    private OwnerAccount testOwner1;
    private OwnerAccount testOwner2;
    private Vehicle testVehicle1;
    private Vehicle testVehicle2;

    @BeforeEach
    void setUp() {
        // Create test owners
        testOwner1 = new OwnerAccount("OWNER001", "owner1@example.com", "John", "Doe");
        testOwner1 = ownerAccountRepository.save(testOwner1);

        testOwner2 = new OwnerAccount("OWNER002", "owner2@example.com", "Jane", "Smith");
        testOwner2 = ownerAccountRepository.save(testOwner2);

        // Create test vehicles
        testVehicle1 = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, testOwner1);
        testVehicle1.setSubscriptionTier(SubscriptionTier.PREMIUM);
        testVehicle1.setCurrentKeyCount(3);
        testVehicle1 = vehicleRepository.save(testVehicle1);

        testVehicle2 = new Vehicle("1HGBH41JXMN109187", "Toyota", "Camry", 2022, testOwner1);
        testVehicle2.setSubscriptionTier(SubscriptionTier.BASIC);
        testVehicle2.setCurrentKeyCount(1);
        testVehicle2 = vehicleRepository.save(testVehicle2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find vehicle by VIN")
    void shouldFindVehicleByVin() {
        Optional<Vehicle> found = vehicleRepository.findByVin("1HGBH41JXMN109186");
        
        assertTrue(found.isPresent());
        assertEquals("1HGBH41JXMN109186", found.get().getVin());
        assertEquals("Honda", found.get().getMake());
        assertEquals("Civic", found.get().getModel());
        assertEquals(2023, found.get().getYear());
    }

    @Test
    @DisplayName("Should return empty when VIN not found")
    void shouldReturnEmptyWhenVinNotFound() {
        Optional<Vehicle> found = vehicleRepository.findByVin("NONEXISTENT");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find vehicles by owner")
    void shouldFindVehiclesByOwner() {
        List<Vehicle> vehiclesForOwner1 = vehicleRepository.findByOwner(testOwner1);
        List<Vehicle> vehiclesForOwner2 = vehicleRepository.findByOwner(testOwner2);
        
        assertEquals(2, vehiclesForOwner1.size());
        assertEquals(0, vehiclesForOwner2.size());
        
        assertTrue(vehiclesForOwner1.stream().anyMatch(v -> "1HGBH41JXMN109186".equals(v.getVin())));
        assertTrue(vehiclesForOwner1.stream().anyMatch(v -> "1HGBH41JXMN109187".equals(v.getVin())));
    }

    @Test
    @DisplayName("Should find vehicles by owner account ID")
    void shouldFindVehiclesByOwnerAccountId() {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerAccountId("OWNER001");
        
        assertEquals(2, vehicles.size());
        assertTrue(vehicles.stream().allMatch(v -> "OWNER001".equals(v.getOwner().getAccountId())));
    }

    @Test
    @DisplayName("Should check if vehicle exists by VIN")
    void shouldCheckIfVehicleExistsByVin() {
        assertTrue(vehicleRepository.existsByVin("1HGBH41JXMN109186"));
        assertTrue(vehicleRepository.existsByVin("1HGBH41JXMN109187"));
        assertFalse(vehicleRepository.existsByVin("NONEXISTENT"));
    }

    @Test
    @DisplayName("Should save and update vehicle")
    void shouldSaveAndUpdateVehicle() {
        Vehicle newVehicle = new Vehicle("1HGBH41JXMN109188", "Ford", "F-150", 2023, testOwner2);
        newVehicle.setColor("Red");
        newVehicle.setLicensePlate("ABC123");
        newVehicle.setSubscriptionTier(SubscriptionTier.ENTERPRISE);
        
        Vehicle saved = vehicleRepository.save(newVehicle);
        
        assertNotNull(saved.getId());
        assertEquals("1HGBH41JXMN109188", saved.getVin());
        assertEquals("Ford", saved.getMake());
        assertEquals("F-150", saved.getModel());
        assertEquals("Red", saved.getColor());
        assertEquals("ABC123", saved.getLicensePlate());
        assertEquals(SubscriptionTier.ENTERPRISE, saved.getSubscriptionTier());
        
        // Update the vehicle
        saved.setColor("Blue");
        saved.setMileage(15000);
        saved.setCurrentKeyCount(5);
        
        Vehicle updated = vehicleRepository.save(saved);
        
        assertEquals("Blue", updated.getColor());
        assertEquals(15000, updated.getMileage());
        assertEquals(5, updated.getCurrentKeyCount());
    }

    @Test
    @DisplayName("Should delete vehicle")
    void shouldDeleteVehicle() {
        assertTrue(vehicleRepository.existsByVin("1HGBH41JXMN109186"));
        
        vehicleRepository.delete(testVehicle1);
        entityManager.flush();
        
        assertFalse(vehicleRepository.existsByVin("1HGBH41JXMN109186"));
    }

    @Test
    @DisplayName("Should handle subscription information")
    void shouldHandleSubscriptionInformation() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        
        assertTrue(vehicle.getSubscriptionActive());
        assertEquals(SubscriptionTier.PREMIUM, vehicle.getSubscriptionTier());
        
        // Update subscription
        vehicle.setSubscriptionActive(false);
        vehicle.setSubscriptionExpiresAt(LocalDateTime.now().minusDays(1));
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertFalse(updated.getSubscriptionActive());
        assertNotNull(updated.getSubscriptionExpiresAt());
    }

    @Test
    @DisplayName("Should handle vehicle status changes")
    void shouldHandleVehicleStatusChanges() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(VehicleStatus.ACTIVE, vehicle.getVehicleStatus());
        
        vehicle.setVehicleStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(VehicleStatus.MAINTENANCE, updated.getVehicleStatus());
    }

    @Test
    @DisplayName("Should handle key count tracking")
    void shouldHandleKeyCountTracking() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(3, vehicle.getCurrentKeyCount());
        assertEquals(20, vehicle.getMaxKeysAllowed()); // Premium tier
        
        vehicle.incrementKeyCount();
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(4, updated.getCurrentKeyCount());
    }

    @Test
    @DisplayName("Should handle usage tracking")
    void shouldHandleUsageTracking() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(0L, vehicle.getTotalKeyUsageCount());
        
        vehicle.incrementKeyUsage();
        vehicle.incrementKeyUsage();
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(2L, updated.getTotalKeyUsageCount());
        assertNotNull(updated.getLastActivityAt());
    }

    @Test
    @DisplayName("Should handle pairing credentials")
    void shouldHandlePairingCredentials() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertNull(vehicle.getPairingPassword());
        assertNull(vehicle.getPairingVerifier());
        
        vehicle.setPairingPassword("test-password");
        vehicle.setPairingVerifier("test-verifier");
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals("test-password", updated.getPairingPassword());
        assertEquals("test-verifier", updated.getPairingVerifier());
    }

    @Test
    @DisplayName("Should handle warranty information")
    void shouldHandleWarrantyInformation() {
        Vehicle vehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertNull(vehicle.getWarrantyExpiresAt());
        
        LocalDateTime warrantyExpiration = LocalDateTime.now().plusYears(3);
        vehicle.setWarrantyExpiresAt(warrantyExpiration);
        vehicle.setPurchaseDate(LocalDateTime.now().minusMonths(6));
        vehicle.setDealerName("Honda Downtown");
        vehicleRepository.save(vehicle);
        
        Vehicle updated = vehicleRepository.findByVin("1HGBH41JXMN109186").get();
        assertEquals(warrantyExpiration, updated.getWarrantyExpiresAt());
        assertNotNull(updated.getPurchaseDate());
        assertEquals("Honda Downtown", updated.getDealerName());
    }

    @Test
    @DisplayName("Should handle complex vehicle details")
    void shouldHandleComplexVehicleDetails() {
        Vehicle vehicle = new Vehicle("1HGBH41JXMN109189", "BMW", "X5", 2023, testOwner2);
        vehicle.setColor("Black");
        vehicle.setLicensePlate("BMW123");
        vehicle.setEngineType("V8");
        vehicle.setTransmissionType("Automatic");
        vehicle.setFuelType("Premium Gasoline");
        vehicle.setMileage(5000);
        vehicle.setSubscriptionTier(SubscriptionTier.ENTERPRISE);
        vehicle.setVehicleStatus(VehicleStatus.ACTIVE);
        vehicle.setCurrentKeyCount(10);
        vehicle.setMaxKeysAllowed(100);
        
        Vehicle saved = vehicleRepository.save(vehicle);
        entityManager.flush();
        
        Vehicle retrieved = vehicleRepository.findByVin("1HGBH41JXMN109189").get();
        
        assertEquals("Black", retrieved.getColor());
        assertEquals("BMW123", retrieved.getLicensePlate());
        assertEquals("V8", retrieved.getEngineType());
        assertEquals("Automatic", retrieved.getTransmissionType());
        assertEquals("Premium Gasoline", retrieved.getFuelType());
        assertEquals(5000, retrieved.getMileage());
        assertEquals(SubscriptionTier.ENTERPRISE, retrieved.getSubscriptionTier());
        assertEquals(VehicleStatus.ACTIVE, retrieved.getVehicleStatus());
        assertEquals(10, retrieved.getCurrentKeyCount());
        assertEquals(100, retrieved.getMaxKeysAllowed());
    }
}
