package com.vehicleoem.service;

import com.vehicleoem.dto.*;
import com.vehicleoem.model.*;
import com.vehicleoem.repository.*;
import com.vehicleoem.client.*;
import com.vehicleoem.security.CryptographyService;
import com.vehicleoem.security.CertificateService;
import com.vehicleoem.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DigitalKeyService Unit Tests")
class DigitalKeyServiceTest {

    @Mock
    private DigitalKeyRepository digitalKeyRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private OwnerAccountRepository ownerAccountRepository;

    @Mock
    private KeyTrackingClient keyTrackingClient;

    @Mock
    private DeviceOemClient deviceOemClient;

    @Mock
    private VehicleTelematicsClient vehicleTelematicsClient;

    @Mock
    private CryptographyService cryptographyService;

    @Mock
    private CertificateService certificateService;

    @Mock
    private EventNotificationService eventNotificationService;

    @Mock
    private ValidationService validationService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private DigitalKeyService digitalKeyService;

    private TrackKeyRequest trackKeyRequest;
    private ManageKeyRequest manageKeyRequest;
    private Vehicle testVehicle;
    private OwnerAccount testOwner;
    private DigitalKey testKey;

    @BeforeEach
    void setUp() {
        // Setup test data
        testOwner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        testOwner.setEmailVerified(true);
        testOwner.setAccountStatus(AccountStatus.ACTIVE);

        testVehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, testOwner);
        testVehicle.setSubscriptionActive(true);
        testVehicle.setSubscriptionTier(SubscriptionTier.PREMIUM);
        testVehicle.setCurrentKeyCount(2);
        testVehicle.setMaxKeysAllowed(20);

        testKey = new DigitalKey("KEY001", "DEVICE001", "Apple", KeyType.OWNER, testVehicle);
        testKey.setStatus(KeyStatus.ACTIVE);

        trackKeyRequest = new TrackKeyRequest();
        trackKeyRequest.setKeyId("KEY001");
        trackKeyRequest.setDeviceId("DEVICE001");
        trackKeyRequest.setDeviceOem("Apple");
        trackKeyRequest.setVehicleId("1HGBH41JXMN109186");
        trackKeyRequest.setKeyType("OWNER");

        manageKeyRequest = new ManageKeyRequest();
        manageKeyRequest.setKeyId("KEY001");
        manageKeyRequest.setAction("SUSPEND");
        manageKeyRequest.setReason("Security concern");
        manageKeyRequest.setRequestedBy("ADMIN");
    }

    @Test
    @DisplayName("Should successfully track new key")
    void shouldSuccessfullyTrackNewKey() {
        // Arrange
        when(vehicleRepository.findByVin("1HGBH41JXMN109186")).thenReturn(Optional.of(testVehicle));
        when(digitalKeyRepository.findByKeyId("KEY001")).thenReturn(Optional.empty());
        when(digitalKeyRepository.save(any(DigitalKey.class))).thenReturn(testKey);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        doNothing().when(validationService).validateTrackKeyRequest(any(), any());
        doNothing().when(validationService).validateVehicleSubscription(any());
        doNothing().when(validationService).validateKeyLimits(any());
        doNothing().when(validationService).validateOwnerAccount(any());

        // Act
        TrackKeyResponse response = digitalKeyService.trackKey(trackKeyRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Key tracked successfully", response.getMessage());
        assertEquals("KEY001", response.getKeyId());

        verify(digitalKeyRepository).save(any(DigitalKey.class));
        verify(vehicleRepository).save(testVehicle);
        verify(keyTrackingClient).registerKey(any(KeyRegistrationRequest.class));
        verify(eventNotificationService).sendKeyTrackedNotification(any(DigitalKey.class));
        verify(auditService).logKeyAction(eq("KEY001"), eq("TRACK_KEY"), eq("SYSTEM"), anyString());
    }

    @Test
    @DisplayName("Should handle duplicate key tracking")
    void shouldHandleDuplicateKeyTracking() {
        // Arrange
        when(vehicleRepository.findByVin("1HGBH41JXMN109186")).thenReturn(Optional.of(testVehicle));
        when(digitalKeyRepository.findByKeyId("KEY001")).thenReturn(Optional.of(testKey));
        doNothing().when(validationService).validateTrackKeyRequest(any(), any());
        doNothing().when(validationService).validateVehicleSubscription(any());
        doNothing().when(validationService).validateKeyLimits(any());
        doNothing().when(validationService).validateOwnerAccount(any());

        // Act
        TrackKeyResponse response = digitalKeyService.trackKey(trackKeyRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Key already tracked", response.getMessage());
        assertEquals("KEY001", response.getKeyId());

        verify(digitalKeyRepository, never()).save(any(DigitalKey.class));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        when(vehicleRepository.findByVin("1HGBH41JXMN109186")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class, () -> {
            digitalKeyService.trackKey(trackKeyRequest);
        });

        verify(digitalKeyRepository, never()).save(any(DigitalKey.class));
    }

    @Test
    @DisplayName("Should successfully manage key - suspend")
    void shouldSuccessfullyManageKeySuspend() {
        // Arrange
        when(digitalKeyRepository.findByKeyId("KEY001")).thenReturn(Optional.of(testKey));
        when(digitalKeyRepository.save(any(DigitalKey.class))).thenReturn(testKey);
        doNothing().when(validationService).validateKeyAction(anyString());
        doNothing().when(validationService).validateKeyStatusTransition(any(), anyString());
        doNothing().when(validationService).validateVehicleSubscription(any());

        // Act
        ManageKeyResponse response = digitalKeyService.manageKey(manageKeyRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Key managed successfully", response.getMessage());
        assertEquals("KEY001", response.getKeyId());

        verify(digitalKeyRepository).save(testKey);
        verify(vehicleTelematicsClient).sendCommand(any(TelematicsCommandRequest.class));
        verify(keyTrackingClient).updateKeyStatus(any(KeyStatusUpdateRequest.class));
        verify(eventNotificationService).sendKeyStatusChangedNotification(any(DigitalKey.class), eq("SUSPEND"));
        verify(auditService).logKeyAction(eq("KEY001"), eq("MANAGE_KEY_SUSPEND"), eq("ADMIN"), eq("Security concern"));
    }
}
