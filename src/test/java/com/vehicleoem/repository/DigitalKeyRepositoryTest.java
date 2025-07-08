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
@DisplayName("DigitalKeyRepository Integration Tests")
class DigitalKeyRepositoryTest {

    private static EmbeddedPostgres embeddedPostgres;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DigitalKeyRepository digitalKeyRepository;

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
    private VehicleRepository vehicleRepository;

    @Autowired
    private OwnerAccountRepository ownerAccountRepository;

    private OwnerAccount testOwner;
    private Vehicle testVehicle;
    private DigitalKey testKey1;
    private DigitalKey testKey2;

    @BeforeEach
    void setUp() {
        // Create test owner
        testOwner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        testOwner = ownerAccountRepository.save(testOwner);

        // Create test vehicle
        testVehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, testOwner);
        testVehicle = vehicleRepository.save(testVehicle);

        // Create test digital keys
        testKey1 = new DigitalKey("OWNER-KEY-001", "DEVICE001", "Apple", KeyType.OWNER, testVehicle);
        testKey1.setStatus(KeyStatus.ACTIVE);
        testKey1 = digitalKeyRepository.save(testKey1);

        testKey2 = new DigitalKey("FRIEND-KEY-002", "DEVICE002", "Samsung", KeyType.FRIEND, testVehicle);
        testKey2.setStatus(KeyStatus.SUSPENDED);
        testKey2.setFriendEmail("friend@example.com");
        testKey2 = digitalKeyRepository.save(testKey2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find digital key by key ID")
    void shouldFindDigitalKeyByKeyId() {
        Optional<DigitalKey> found = digitalKeyRepository.findByKeyId("OWNER-KEY-001");

        assertTrue(found.isPresent());
        assertEquals("OWNER-KEY-001", found.get().getKeyId());
        assertEquals("DEVICE001", found.get().getDeviceId());
        assertEquals(KeyType.OWNER, found.get().getKeyType());
    }

    @Test
    @DisplayName("Should return empty when key ID not found")
    void shouldReturnEmptyWhenKeyIdNotFound() {
        Optional<DigitalKey> found = digitalKeyRepository.findByKeyId("NONEXISTENT");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find keys by vehicle")
    void shouldFindKeysByVehicle() {
        List<DigitalKey> keys = digitalKeyRepository.findByVehicle(testVehicle);
        
        assertEquals(2, keys.size());
        assertTrue(keys.stream().anyMatch(key -> "KEY001".equals(key.getKeyId())));
        assertTrue(keys.stream().anyMatch(key -> "KEY002".equals(key.getKeyId())));
    }

    @Test
    @DisplayName("Should find keys by device ID")
    void shouldFindKeysByDeviceId() {
        List<DigitalKey> keys = digitalKeyRepository.findByDeviceId("DEVICE001");
        
        assertEquals(1, keys.size());
        assertEquals("KEY001", keys.get(0).getKeyId());
    }

    @Test
    @DisplayName("Should find keys by status")
    void shouldFindKeysByStatus() {
        List<DigitalKey> activeKeys = digitalKeyRepository.findByStatus(KeyStatus.ACTIVE);
        List<DigitalKey> suspendedKeys = digitalKeyRepository.findByStatus(KeyStatus.SUSPENDED);
        
        assertEquals(1, activeKeys.size());
        assertEquals("KEY001", activeKeys.get(0).getKeyId());
        
        assertEquals(1, suspendedKeys.size());
        assertEquals("KEY002", suspendedKeys.get(0).getKeyId());
    }

    @Test
    @DisplayName("Should find keys by vehicle VIN")
    void shouldFindKeysByVehicleVin() {
        List<DigitalKey> keys = digitalKeyRepository.findByVehicleVin("1HGBH41JXMN109186");
        
        assertEquals(2, keys.size());
        assertTrue(keys.stream().allMatch(key -> "1HGBH41JXMN109186".equals(key.getVehicle().getVin())));
    }

    @Test
    @DisplayName("Should find keys by vehicle VIN and key type")
    void shouldFindKeysByVehicleVinAndKeyType() {
        List<DigitalKey> ownerKeys = digitalKeyRepository.findByVehicleVinAndKeyType("1HGBH41JXMN109186", KeyType.OWNER);
        List<DigitalKey> friendKeys = digitalKeyRepository.findByVehicleVinAndKeyType("1HGBH41JXMN109186", KeyType.FRIEND);
        
        assertEquals(1, ownerKeys.size());
        assertEquals("KEY001", ownerKeys.get(0).getKeyId());
        assertEquals(KeyType.OWNER, ownerKeys.get(0).getKeyType());
        
        assertEquals(1, friendKeys.size());
        assertEquals("KEY002", friendKeys.get(0).getKeyId());
        assertEquals(KeyType.FRIEND, friendKeys.get(0).getKeyType());
    }

    @Test
    @DisplayName("Should find keys by key type")
    void shouldFindKeysByKeyType() {
        List<DigitalKey> friendKeys = digitalKeyRepository.findByKeyType(KeyType.FRIEND);
        
        assertEquals(1, friendKeys.size());
        assertEquals("KEY002", friendKeys.get(0).getKeyId());
        assertEquals(KeyType.FRIEND, friendKeys.get(0).getKeyType());
    }

    @Test
    @DisplayName("Should check if key exists by key ID")
    void shouldCheckIfKeyExistsByKeyId() {
        assertTrue(digitalKeyRepository.existsByKeyId("KEY001"));
        assertTrue(digitalKeyRepository.existsByKeyId("KEY002"));
        assertFalse(digitalKeyRepository.existsByKeyId("NONEXISTENT"));
    }

    @Test
    @DisplayName("Should save and update digital key")
    void shouldSaveAndUpdateDigitalKey() {
        DigitalKey newKey = new DigitalKey("FRIEND-KEY-003", "DEVICE003", "Google", KeyType.FRIEND, testVehicle);
        newKey.setFriendEmail("newFriend@example.com");
        newKey.setPermissionLevel(PermissionLevel.UNLOCK_ONLY);

        DigitalKey saved = digitalKeyRepository.save(newKey);

        assertNotNull(saved.getId());
        assertEquals("FRIEND-KEY-003", saved.getKeyId());
        assertEquals("newFriend@example.com", saved.getFriendEmail());
        assertEquals(PermissionLevel.UNLOCK_ONLY, saved.getPermissionLevel());
        
        // Update the key
        saved.setStatus(KeyStatus.SUSPENDED);
        saved.setPermissionLevel(PermissionLevel.DRIVE_ONLY);
        
        DigitalKey updated = digitalKeyRepository.save(saved);
        
        assertEquals(KeyStatus.SUSPENDED, updated.getStatus());
        assertEquals(PermissionLevel.DRIVE_ONLY, updated.getPermissionLevel());
    }

    @Test
    @DisplayName("Should delete digital key")
    void shouldDeleteDigitalKey() {
        assertTrue(digitalKeyRepository.existsByKeyId("OWNER-KEY-001"));

        digitalKeyRepository.delete(testKey1);
        entityManager.flush();

        assertFalse(digitalKeyRepository.existsByKeyId("OWNER-KEY-001"));
    }

    @Test
    @DisplayName("Should handle cascading operations with vehicle")
    void shouldHandleCascadingOperationsWithVehicle() {
        // Create another vehicle with keys
        Vehicle anotherVehicle = new Vehicle("1HGBH41JXMN109187", "Toyota", "Camry", 2023, testOwner);
        anotherVehicle = vehicleRepository.save(anotherVehicle);
        
        DigitalKey keyForAnotherVehicle = new DigitalKey("KEY003", "DEVICE003", "Google", KeyType.OWNER, anotherVehicle);
        digitalKeyRepository.save(keyForAnotherVehicle);
        
        entityManager.flush();
        
        // Verify keys are associated with correct vehicles
        List<DigitalKey> keysForOriginalVehicle = digitalKeyRepository.findByVehicle(testVehicle);
        List<DigitalKey> keysForAnotherVehicle = digitalKeyRepository.findByVehicle(anotherVehicle);
        
        assertEquals(2, keysForOriginalVehicle.size());
        assertEquals(1, keysForAnotherVehicle.size());
        assertEquals("KEY003", keysForAnotherVehicle.get(0).getKeyId());
    }

    @Test
    @DisplayName("Should handle complex queries with multiple conditions")
    void shouldHandleComplexQueriesWithMultipleConditions() {
        // Create additional test data
        DigitalKey expiredKey = new DigitalKey("KEY004", "DEVICE004", "Apple", KeyType.FRIEND, testVehicle);
        expiredKey.setStatus(KeyStatus.EXPIRED);
        expiredKey.setExpiresAt(LocalDateTime.now().minusDays(1));
        digitalKeyRepository.save(expiredKey);
        
        DigitalKey terminatedKey = new DigitalKey("KEY005", "DEVICE005", "Samsung", KeyType.OWNER, testVehicle);
        terminatedKey.setStatus(KeyStatus.TERMINATED);
        digitalKeyRepository.save(terminatedKey);
        
        entityManager.flush();
        
        // Test various combinations
        List<DigitalKey> allKeysForVehicle = digitalKeyRepository.findByVehicle(testVehicle);
        assertEquals(4, allKeysForVehicle.size());
        
        List<DigitalKey> friendKeysForVehicle = digitalKeyRepository.findByVehicleVinAndKeyType(testVehicle.getVin(), KeyType.FRIEND);
        assertEquals(2, friendKeysForVehicle.size());
        
        List<DigitalKey> expiredKeys = digitalKeyRepository.findByStatus(KeyStatus.EXPIRED);
        assertEquals(1, expiredKeys.size());
        assertEquals("KEY004", expiredKeys.get(0).getKeyId());
    }
}
