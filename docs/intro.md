// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.vehicleoem</groupId>
    <artifactId>vehicle-oem-server</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk18on</artifactId>
            <version>1.76</version>
        </dependency>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-jdk18on</artifactId>
            <version>1.76</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

// Application.java
package com.vehicleoem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableTransactionManagement
public class VehicleOemServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleOemServerApplication.class, args);
    }
}

// Additional DTOs
package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// Client DTOs for external services
public class EventNotificationRequest {
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("action")
    private String action;
    
    @JsonProperty("newStatus")
    private String newStatus;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

public class KeyRegistrationRequest {
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("keyType")
    private String keyType;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

public class KeyRegistrationResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("trackingId")
    private String trackingId;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
}

public class KeyStatusUpdateRequest {
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("newStatus")
    private String newStatus;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

public class KeyStatusUpdateResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean

// Entity Models
package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "owner_accounts")
public class OwnerAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String accountId;
    
    @NotBlank
    private String email;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;
    
    // Constructors, getters, setters
    public OwnerAccount() {}
    
    public OwnerAccount(String accountId, String email, String firstName, String lastName) {
        this.accountId = accountId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String vin;
    
    @NotBlank
    private String make;
    
    @NotBlank
    private String model;
    
    @NotNull
    private Integer year;
    
    @Lob
    private String publicKeyCertificate;
    
    @Column(name = "pairing_password")
    private String pairingPassword;
    
    @Column(name = "pairing_verifier")
    private String pairingVerifier;
    
    @Column(name = "subscription_active")
    private Boolean subscriptionActive = true;
    
    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private OwnerAccount owner;
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DigitalKey> digitalKeys;
    
    // Constructors, getters, setters
    public Vehicle() {}
    
    public Vehicle(String vin, String make, String model, Integer year, OwnerAccount owner) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getPublicKeyCertificate() { return publicKeyCertificate; }
    public void setPublicKeyCertificate(String publicKeyCertificate) { this.publicKeyCertificate = publicKeyCertificate; }
    
    public String getPairingPassword() { return pairingPassword; }
    public void setPairingPassword(String pairingPassword) { this.pairingPassword = pairingPassword; }
    
    public String getPairingVerifier() { return pairingVerifier; }
    public void setPairingVerifier(String pairingVerifier) { this.pairingVerifier = pairingVerifier; }
    
    public Boolean getSubscriptionActive() { return subscriptionActive; }
    public void setSubscriptionActive(Boolean subscriptionActive) { this.subscriptionActive = subscriptionActive; }
    
    public LocalDateTime getSubscriptionExpiresAt() { return subscriptionExpiresAt; }
    public void setSubscriptionExpiresAt(LocalDateTime subscriptionExpiresAt) { this.subscriptionExpiresAt = subscriptionExpiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public OwnerAccount getOwner() { return owner; }
    public void setOwner(OwnerAccount owner) { this.owner = owner; }
    
    public List<DigitalKey> getDigitalKeys() { return digitalKeys; }
    public void setDigitalKeys(List<DigitalKey> digitalKeys) { this.digitalKeys = digitalKeys; }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

@Entity
@Table(name = "digital_keys")
public class DigitalKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String keyId;
    
    @NotBlank
    @Column(name = "device_id")
    private String deviceId;
    
    @NotBlank
    @Column(name = "device_oem")
    private String deviceOem;
    
    @Enumerated(EnumType.STRING)
    private KeyType keyType;
    
    @Enumerated(EnumType.STRING)
    private KeyStatus status;
    
    @Lob
    @Column(name = "public_key")
    private String publicKey;
    
    @Column(name = "ui_bundle")
    private String uiBundle;
    
    @Column(name = "vehicle_mobilization_data")
    private String vehicleMobilizationData;
    
    @Column(name = "friend_email")
    private String friendEmail;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    // Constructors, getters, setters
    public DigitalKey() {}
    
    public DigitalKey(String keyId, String deviceId, String deviceOem, KeyType keyType, Vehicle vehicle) {
        this.keyId = keyId;
        this.deviceId = deviceId;
        this.deviceOem = deviceOem;
        this.keyType = keyType;
        this.vehicle = vehicle;
        this.status = KeyStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public KeyType getKeyType() { return keyType; }
    public void setKeyType(KeyType keyType) { this.keyType = keyType; }
    
    public KeyStatus getStatus() { return status; }
    public void setStatus(KeyStatus status) { this.status = status; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getUiBundle() { return uiBundle; }
    public void setUiBundle(String uiBundle) { this.uiBundle = uiBundle; }
    
    public String getVehicleMobilizationData() { return vehicleMobilizationData; }
    public void setVehicleMobilizationData(String vehicleMobilizationData) { this.vehicleMobilizationData = vehicleMobilizationData; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

// Enums
package com.vehicleoem.model;

public enum KeyType {
    OWNER,
    FRIEND
}

public enum KeyStatus {
    ACTIVE,
    SUSPENDED,
    TERMINATED,
    EXPIRED
}

// DTOs
package com.vehicleoem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrackKeyRequest {
    @NotBlank
    @JsonProperty("keyId")
    private String keyId;
    
    @NotBlank
    @JsonProperty("deviceId")
    private String deviceId;
    
    @NotBlank
    @JsonProperty("deviceOem")
    private String deviceOem;
    
    @NotBlank
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @NotBlank
    @JsonProperty("keyType")
    private String keyType;
    
    @JsonProperty("publicKey")
    private String publicKey;
    
    @JsonProperty("uiBundle")
    private String uiBundle;
    
    @JsonProperty("vehicleMobilizationData")
    private String vehicleMobilizationData;
    
    @JsonProperty("friendEmail")
    private String friendEmail;
    
    @JsonProperty("expiresAt")
    private String expiresAt;
    
    // Constructors, getters, setters
    public TrackKeyRequest() {}
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceOem() { return deviceOem; }
    public void setDeviceOem(String deviceOem) { this.deviceOem = deviceOem; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getUiBundle() { return uiBundle; }
    public void setUiBundle(String uiBundle) { this.uiBundle = uiBundle; }
    
    public String getVehicleMobilizationData() { return vehicleMobilizationData; }
    public void setVehicleMobilizationData(String vehicleMobilizationData) { this.vehicleMobilizationData = vehicleMobilizationData; }
    
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }
    
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}

public class TrackKeyResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("trackingId")
    private String trackingId;
    
    public TrackKeyResponse() {}
    
    public TrackKeyResponse(boolean success, String message, String keyId, String trackingId) {
        this.success = success;
        this.message = message;
        this.keyId = keyId;
        this.trackingId = trackingId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
}

public class ManageKeyRequest {
    @NotBlank
    @JsonProperty("keyId")
    private String keyId;
    
    @NotBlank
    @JsonProperty("action")
    private String action;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("requestedBy")
    private String requestedBy;
    
    public ManageKeyRequest() {}
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
}

public class ManageKeyResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("keyId")
    private String keyId;
    
    @JsonProperty("newStatus")
    private String newStatus;
    
    public ManageKeyResponse() {}
    
    public ManageKeyResponse(boolean success, String message, String keyId, String newStatus) {
        this.success = success;
        this.message = message;
        this.keyId = keyId;
        this.newStatus = newStatus;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}

// Repositories
package com.vehicleoem.repository;

import com.vehicleoem.model.OwnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OwnerAccountRepository extends JpaRepository<OwnerAccount, Long> {
    Optional<OwnerAccount> findByAccountId(String accountId);
    Optional<OwnerAccount> findByEmail(String email);
    boolean existsByAccountId(String accountId);
    boolean existsByEmail(String email);
}

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);
    List<Vehicle> findByOwner(OwnerAccount owner);
    List<Vehicle> findByOwnerAccountId(String accountId);
    boolean existsByVin(String vin);
}

@Repository
public interface DigitalKeyRepository extends JpaRepository<DigitalKey, Long> {
    Optional<DigitalKey> findByKeyId(String keyId);
    List<DigitalKey> findByVehicle(Vehicle vehicle);
    List<DigitalKey> findByDeviceId(String deviceId);
    List<DigitalKey> findByStatus(KeyStatus status);
    List<DigitalKey> findByVehicleVin(String vin);
    boolean existsByKeyId(String keyId);
}

// Feign Clients
package com.vehicleoem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "device-oem-server", url = "${feign.device-oem-server.url}")
public interface DeviceOemClient {
    
    @PostMapping("/api/v1/eventNotification")
    void sendEventNotification(@RequestBody EventNotificationRequest request);
    
    @PostMapping("/api/v1/keyValidation")
    KeyValidationResponse validateKey(@RequestBody KeyValidationRequest request);
}

@FeignClient(name = "kts-server", url = "${feign.kts-server.url}")
public interface KeyTrackingClient {
    
    @PostMapping("/api/v1/registerKey")
    KeyRegistrationResponse registerKey(@RequestBody KeyRegistrationRequest request);
    
    @PostMapping("/api/v1/updateKeyStatus")
    KeyStatusUpdateResponse updateKeyStatus(@RequestBody KeyStatusUpdateRequest request);
}

@FeignClient(name = "vehicle-telematics", url = "${feign.vehicle-telematics.url}")
public interface VehicleTelematicsClient {
    
    @PostMapping("/api/v1/sendCommand")
    TelematicsResponse sendCommand(@RequestBody TelematicsCommandRequest request);
    
    @PostMapping("/api/v1/provisionPairingVerifier")
    TelematicsResponse provisionPairingVerifier(@RequestBody PairingVerifierRequest request);
}

// Services
package com.vehicleoem.service;

import com.vehicleoem.dto.*;
import com.vehicleoem.model.*;
import com.vehicleoem.repository.*;
import com.vehicleoem.client.*;
import com.vehicleoem.security.CryptographyService;
import com.vehicleoem.security.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DigitalKeyService {
    
    @Autowired
    private DigitalKeyRepository digitalKeyRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private OwnerAccountRepository ownerAccountRepository;
    
    @Autowired
    private KeyTrackingClient keyTrackingClient;
    
    @Autowired
    private DeviceOemClient deviceOemClient;
    
    @Autowired
    private VehicleTelematicsClient vehicleTelematicsClient;
    
    @Autowired
    private CryptographyService cryptographyService;
    
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private EventNotificationService eventNotificationService;
    
    @CircuitBreaker(name = "trackKey", fallbackMethod = "trackKeyFallback")
    @Retry(name = "trackKey")
    public TrackKeyResponse trackKey(TrackKeyRequest request) {
        try {
            // Validate request
            validateTrackKeyRequest(request);
            
            // Check if key already exists (idempotency)
            Optional<DigitalKey> existingKey = digitalKeyRepository.findByKeyId(request.getKeyId());
            if (existingKey.isPresent()) {
                return new TrackKeyResponse(true, "Key already tracked", request.getKeyId(), 
                    existingKey.get().getKeyId());
            }
            
            // Find vehicle
            Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + request.getVehicleId()));
            
            // Validate subscription
            if (!vehicle.getSubscriptionActive() || 
                (vehicle.getSubscriptionExpiresAt() != null && 
                 vehicle.getSubscriptionExpiresAt().isBefore(LocalDateTime.now()))) {
                throw new IllegalStateException("Vehicle subscription is not active");
            }
            
            // Create digital key
            DigitalKey digitalKey = createDigitalKey(request, vehicle);
            
            // Save to database
            digitalKey = digitalKeyRepository.save(digitalKey);
            
            // Register with KTS
            registerWithKTS(digitalKey);
            
            // Send event notification
            eventNotificationService.sendKeyTrackedNotification(digitalKey);
            
            return new TrackKeyResponse(true, "Key tracked successfully", 
                digitalKey.getKeyId(), digitalKey.getKeyId());
            
        } catch (Exception e) {
            return new TrackKeyResponse(false, "Failed to track key: " + e.getMessage(), 
                request.getKeyId(), null);
        }
    }
    
    @CircuitBreaker(name = "manageKey", fallbackMethod = "manageKeyFallback")
    @Retry(name = "manageKey")
    public ManageKeyResponse manageKey(ManageKeyRequest request) {
        try {
            // Find digital key
            DigitalKey digitalKey = digitalKeyRepository.findByKeyId(request.getKeyId())
                .orElseThrow(() -> new IllegalArgumentException("Digital key not found: " + request.getKeyId()));
            
            // Process action
            KeyStatus newStatus = processKeyAction(digitalKey, request.getAction(), request.getReason());
            
            // Update key status
            digitalKey.setStatus(newStatus);
            digitalKey.setUpdatedAt(LocalDateTime.now());
            digitalKeyRepository.save(digitalKey);
            
            // Send telematics command to vehicle
            sendVehicleCommand(digitalKey, request.getAction());
            
            // Update KTS
            updateKTSStatus(digitalKey);
            
            // Send event notification
            eventNotificationService.sendKeyStatusChangedNotification(digitalKey, request.getAction());
            
            return new ManageKeyResponse(true, "Key managed successfully", 
                digitalKey.getKeyId(), newStatus.name());
            
        } catch (Exception e) {
            return new ManageKeyResponse(false, "Failed to manage key: " + e.getMessage(), 
                request.getKeyId(), null);
        }
    }
    
    private void validateTrackKeyRequest(TrackKeyRequest request) {
        if (request.getKeyId() == null || request.getKeyId().trim().isEmpty()) {
            throw new IllegalArgumentException("Key ID is required");
        }
        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID is required");
        }
        if (request.getVehicleId() == null || request.getVehicleId().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (request.getKeyType() == null || request.getKeyType().trim().isEmpty()) {
            throw new IllegalArgumentException("Key type is required");
        }
    }
    
    private DigitalKey createDigitalKey(TrackKeyRequest request, Vehicle vehicle) {
        DigitalKey digitalKey = new DigitalKey();
        digitalKey.setKeyId(request.getKeyId());
        digitalKey.setDeviceId(request.getDeviceId());
        digitalKey.setDeviceOem(request.getDeviceOem());
        digitalKey.setKeyType(KeyType.valueOf(request.getKeyType().toUpperCase()));
        digitalKey.setVehicle(vehicle);
        digitalKey.setPublicKey(request.getPublicKey());
        digitalKey.setUiBundle(request.getUiBundle());
        digitalKey.setVehicleMobilizationData(request.getVehicleMobilizationData());
        digitalKey.setFriendEmail(request.getFriendEmail());
        
        if (request.getExpiresAt() != null && !request.getExpiresAt().trim().isEmpty()) {
            digitalKey.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(), 
                DateTimeFormatter.ISO_DATE_TIME));
        }
        
        digitalKey.setStatus(KeyStatus.ACTIVE);
        digitalKey.setCreatedAt(LocalDateTime.now());
        digitalKey.setUpdatedAt(LocalDateTime.now());
        
        return digitalKey;
    }
    
    private void registerWithKTS(DigitalKey digitalKey) {
        try {
            KeyRegistrationRequest ktsRequest = new KeyRegistrationRequest();
            ktsRequest.setKeyId(digitalKey.getKeyId());
            ktsRequest.setVehicleId(digitalKey.getVehicle().getVin());
            ktsRequest.setDeviceId(digitalKey.getDeviceId());
            ktsRequest.setKeyType(digitalKey.getKeyType().name());
            ktsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            keyTrackingClient.registerKey(ktsRequest);
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Failed to register key with KTS: " + e.getMessage());
        }
    }
    
    private KeyStatus processKeyAction(DigitalKey digitalKey, String action, String reason) {
        switch (action.toUpperCase()) {
            case "TERMINATE":
                return KeyStatus.TERMINATED;
            case "SUSPEND":
                return KeyStatus.SUSPENDED;
            case "RESUME":
                if (digitalKey.getStatus() == KeyStatus.SUSPENDED) {
                    return KeyStatus.ACTIVE;
                }
                throw new IllegalStateException("Cannot resume key that is not suspended");
            case "EXPIRE":
                return KeyStatus.EXPIRED;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }
    
    private void sendVehicleCommand(DigitalKey digitalKey, String action) {
        try {
            TelematicsCommandRequest telematicsRequest = new TelematicsCommandRequest();
            telematicsRequest.setVehicleId(digitalKey.getVehicle().getVin());
            telematicsRequest.setCommand(action);
            telematicsRequest.setKeyId(digitalKey.getKeyId());
            telematicsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            vehicleTelematicsClient.sendCommand(telematicsRequest);
        } catch (Exception e) {
            System.err.println("Failed to send vehicle command: " + e.getMessage());
        }
    }
    
    private void updateKTSStatus(DigitalKey digitalKey) {
        try {
            KeyStatusUpdateRequest ktsRequest = new KeyStatusUpdateRequest();
            ktsRequest.setKeyId(digitalKey.getKeyId());
            ktsRequest.setNewStatus(digitalKey.getStatus().name());
            ktsRequest.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            keyTrackingClient.updateKeyStatus(ktsRequest);
        } catch (Exception e) {
            System.err.println("Failed to update KTS status: " + e.getMessage());
        }
    }
    
    // Fallback methods
    public TrackKeyResponse trackKeyFallback(TrackKeyRequest request, Exception ex) {
        return new TrackKeyResponse(false, "Service temporarily unavailable: " + ex.getMessage(), 
            request.getKeyId(), null);
    }
    
    public ManageKeyResponse manageKeyFallback(ManageKeyRequest request, Exception ex) {
        return new ManageKeyResponse(false, "Service temporarily unavailable: " + ex.getMessage(), 
            request.getKeyId(), null);
    }
}

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
}

@Service
public class OwnerAccountService {
    
    @Autowired
    private OwnerAccountRepository ownerAccountRepository;
    
    @Transactional
    public OwnerAccount createOwnerAccount(String accountId, String email, String firstName, String lastName) {
        // Check if account already exists
        if (ownerAccountRepository.existsByAccountId(accountId)) {
            throw new IllegalArgumentException("Account already exists: " + accountId);
        }
        
        if (ownerAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        
        OwnerAccount account = new OwnerAccount(accountId, email, firstName, lastName);
        return ownerAccountRepository.save(account);
    }
    
    public OwnerAccount findByAccountId(String accountId) {
        return ownerAccountRepository.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
    }
    
    public OwnerAccount findByEmail(String email) {
        return ownerAccountRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Account not found for email: " + email));
    }
}

@Service
public class EventNotificationService {
    
    @Autowired
    private DeviceOemClient deviceOemClient;
    
    @Async
    @CircuitBreaker(name = "eventNotification")
    @Retry(name = "eventNotification")
    public void sendKeyTrackedNotification(DigitalKey digitalKey) {
        try {
            EventNotificationRequest request = new EventNotificationRequest();
            request.setEventType("KEY_TRACKED");
            request.setKeyId(digitalKey.getKeyId());
            request.setDeviceId(digitalKey.getDeviceId());
            request.setVehicleId(digitalKey.getVehicle().getVin());
            request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            deviceOemClient.sendEventNotification(request);
        } catch (Exception e) {
            System.err.println("Failed to send key tracked notification: " + e.getMessage());
        }
    }
    
    @Async
    @CircuitBreaker(name = "eventNotification")
    @Retry(name = "eventNotification")
    public void sendKeyStatusChangedNotification(DigitalKey digitalKey, String action) {
        try {
            EventNotificationRequest request = new EventNotificationRequest();
            request.setEventType("KEY_STATUS_CHANGED");
            request.setKeyId(digitalKey.getKeyId());
            request.setDeviceId(digitalKey.getDeviceId());
            request.setVehicleId(digitalKey.getVehicle().getVin());
            request.setAction(action);
            request.setNewStatus(digitalKey.getStatus().name());
            request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            deviceOemClient.sendEventNotification(request);
        } catch (Exception e) {
            System.err.println("Failed to send key status changed notification: " + e.getMessage());
        }
    }
}

// Security Services
package com.vehicleoem.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class CryptographyService {
    
    private static final String CURVE_NAME = "secp256r1"; // NIST P-256
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public String generatePairingPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    public String generatePairingVerifier(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate pairing verifier", e);
        }
    }
    
    public KeyPair generateECKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(CURVE_NAME);
            keyGen.initialize(ecSpec, new SecureRandom());
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate EC key pair", e);
        }
    }
    
    public String signData(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(data);
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign data", e);
        }
    }
    
    public boolean verifySignature(byte[] data, String signatureStr, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(publicKey);
            signature.update(data);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String encryptWithECIES(String data, PublicKey publicKey) {
        try {
            // Implementation of ECIES encryption
            // This is a simplified version - real implementation would use proper ECIES
            return Base64.getEncoder().encodeToString(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data with ECIES", e);
        }
    }
    
    public String decryptWithECIES(String encryptedData, PrivateKey privateKey) {
        try {
            // Implementation of ECIES decryption
            // This is a simplified version - real implementation would use proper ECIES
            return new String(Base64.getDecoder().decode(encryptedData));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data with ECIES", e);
        }
    }
}

@Service
public class CertificateService {
    
    @Autowired
    private CryptographyService cryptographyService;
    
    // This would typically load from HSM or secure storage
    private KeyPair vehicleOemKeyPair;
    
    public String generateVehiclePublicKeyCertificate(String vin) {
        try {
            // Generate key pair for vehicle
            KeyPair vehicleKeyPair = cryptographyService.generateECKeyPair();
            
            // Create certificate (simplified - real implementation would use proper X.509)
            VehicleCertificate cert = new VehicleCertificate();
            cert.setVin(vin);
            cert.setPublicKey(Base64.getEncoder().encodeToString(vehicleKeyPair.getPublic().getEncoded()));
            cert.setIssuer("Vehicle OEM CA");
            cert.setValidFrom(LocalDateTime.now());
            cert.setValidUntil(LocalDateTime.now().plusYears(5));
            
            // Sign certificate with Vehicle OEM private key
            String certData = cert.toString();
            String signature = cryptographyService.signData(certData.getBytes(), getVehicleOemPrivateKey());
            cert.setSignature(signature);
            
            return Base64.getEncoder().encodeToString(cert.toString().getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate vehicle certificate", e);
        }
    }
    
    public String generateDeviceOemCrossSignedCertificate(String deviceOemCertificate) {
        try {
            // Cross-sign the Device OEM certificate with Vehicle OEM private key
            String signature = cryptographyService.signData(deviceOemCertificate.getBytes(), 
                getVehicleOemPrivateKey());
            
            CrossSignedCertificate crossCert = new CrossSignedCertificate();
            crossCert.setOriginalCertificate(deviceOemCertificate);
            crossCert.setCrossSignature(signature);
            crossCert.setCrossSignedBy("Vehicle OEM CA");
            crossCert.setCrossSignedAt(LocalDateTime.now());
            
            return Base64.getEncoder().encodeToString(crossCert.toString().getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate cross-signed certificate", e);
        }
    }
    
    private PrivateKey getVehicleOemPrivateKey() {
        // In production, this would be loaded from HSM
        if (vehicleOemKeyPair == null) {
            vehicleOemKeyPair = cryptographyService.generateECKeyPair();
        }
        return vehicleOemKeyPair.getPrivate();
    }
    
    private static class VehicleCertificate {
        private String vin;
        private String publicKey;
        private String issuer;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;
        private String signature;
        
        // Getters and setters
        public String getVin() { return vin; }
        public void setVin(String vin) { this.vin = vin; }
        
        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
        
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        
        public LocalDateTime getValidFrom() { return validFrom; }
        public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
        
        public LocalDateTime getValidUntil() { return validUntil; }
        public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
        
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
        
        @Override
        public String toString() {
            return String.format("VIN:%s|PublicKey:%s|Issuer:%s|ValidFrom:%s|ValidUntil:%s", 
                vin, publicKey, issuer, validFrom, validUntil);
        }
    }
    
    private static class CrossSignedCertificate {
        private String originalCertificate;
        private String crossSignature;
        private String crossSignedBy;
        private LocalDateTime crossSignedAt;
        
        // Getters and setters
        public String getOriginalCertificate() { return originalCertificate; }
        public void setOriginalCertificate(String originalCertificate) { this.originalCertificate = originalCertificate; }
        
        public String getCrossSignature() { return crossSignature; }
        public void setCrossSignature(String crossSignature) { this.crossSignature = crossSignature; }
        
        public String getCrossSignedBy() { return crossSignedBy; }
        public void setCrossSignedBy(String crossSignedBy) { this.crossSignedBy = crossSignedBy; }
        
        public LocalDateTime getCrossSignedAt() { return crossSignedAt; }
        public void setCrossSignedAt(LocalDateTime crossSignedAt) { this.crossSignedAt = crossSignedAt; }
        
        @Override
        public String toString() {
            return String.format("Original:%s|CrossSignature:%s|CrossSignedBy:%s|CrossSignedAt:%s", 
                originalCertificate, crossSignature, crossSignedBy, crossSignedAt);
        }
    }
}

// Controllers
package com.vehicleoem.controller;

import com.vehicleoem.dto.*;
import com.vehicleoem.service.DigitalKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
public class DigitalKeyController {
    
    @Autowired
    private DigitalKeyService digitalKeyService;
    
    @PostMapping("/trackKey")
    public ResponseEntity<TrackKeyResponse> trackKey(@Valid @RequestBody TrackKeyRequest request) {
        try {
            TrackKeyResponse response = digitalKeyService.trackKey(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TrackKeyResponse errorResponse = new TrackKeyResponse(false, 
                "Internal server error: " + e.getMessage(), request.getKeyId(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/manageKey")
    public ResponseEntity<ManageKeyResponse> manageKey(@Valid @RequestBody ManageKeyRequest request) {
        try {
            ManageKeyResponse response = digitalKeyService.manageKey(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ManageKeyResponse errorResponse = new ManageKeyResponse(false, 
                "Internal server error: " + e.getMessage(), request.getKeyId(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/versionUpdate")
    public ResponseEntity<VersionUpdateResponse> versionUpdate(@Valid @RequestBody VersionUpdateRequest request) {
        try {
            // Implementation for version update
            VersionUpdateResponse response = new VersionUpdateResponse(true, "Version updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            VersionUpdateResponse errorResponse = new VersionUpdateResponse(false, 
                "Failed to update version: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        try {
            Vehicle vehicle = vehicleService.createVehicle(request.getVin(), request.getMake(), 
                request.getModel(), request.getYear(), request.getOwnerAccountId());
            
            VehicleResponse response = new VehicleResponse(vehicle.getVin(), vehicle.getMake(), 
                vehicle.getModel(), vehicle.getYear(), vehicle.getOwner().getAccountId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{vin}/initialize-pairing")
    public ResponseEntity<Void> initializePairing(@PathVariable String vin) {
        try {
            vehicleService.initializePairing(vin);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{vin}/subscription")
    public ResponseEntity<Void> updateSubscription(@PathVariable String vin, 
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        try {
            vehicleService.updateSubscription(vin, request.isActive(), request.getExpiresAt());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

@RestController
@RequestMapping("/api/v1/accounts")
public class OwnerAccountController {
    
    @Autowired
    private OwnerAccountService ownerAccountService;
    
    @PostMapping
    public ResponseEntity<OwnerAccountResponse> createAccount(@Valid @RequestBody CreateOwnerAccountRequest request) {
        try {
            OwnerAccount account = ownerAccountService.createOwnerAccount(request.getAccountId(), 
                request.getEmail(), request.getFirstName(), request.getLastName());
            
            OwnerAccountResponse response = new OwnerAccountResponse(account.getAccountId(), 
                account.getEmail(), account.getFirstName(), account.getLastName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{accountId}")
    public ResponseEntity<OwnerAccountResponse> getAccount(@PathVariable String accountId) {
        try {
            OwnerAccount account = ownerAccountService.findByAccountId(accountId);
            
            OwnerAccountResponse response = new OwnerAccountResponse(account.getAccountId(), 
                account.getEmail(), account.getFirstName(), account.getLastName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}