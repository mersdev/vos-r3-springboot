package com.vehicleoem.integration;

import com.vehicleoem.config.BaseTest;
import com.vehicleoem.dto.*;
import com.vehicleoem.model.*;
import com.vehicleoem.repository.*;
import com.vehicleoem.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
    "feign.device-oem-server.url=http://localhost:9999",
    "feign.kts-server.url=http://localhost:9999",
    "feign.vehicle-telematics.url=http://localhost:9999"
})
@Transactional
@DisplayName("Business Scenario Integration Tests")
class BusinessScenarioIntegrationTest extends BaseTest {

    @Autowired
    private OwnerAccountService ownerAccountService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DigitalKeyService digitalKeyService;

    @Autowired
    private KeySharingService keySharingService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private VehiclePairingService vehiclePairingService;

    @Autowired
    private OwnerAccountRepository ownerAccountRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DigitalKeyRepository digitalKeyRepository;

    private OwnerAccount testOwner;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Create test owner account directly
        testOwner = new OwnerAccount("OWNER001", "owner@example.com", "John", "Doe");
        testOwner.setEmailVerified(true);
        testOwner.setAccountStatus(AccountStatus.ACTIVE);
        testOwner = ownerAccountRepository.save(testOwner);

        // Create test vehicle directly
        testVehicle = new Vehicle("1HGBH41JXMN109186", "Honda", "Civic", 2023, testOwner);
        testVehicle.setSubscriptionActive(true);
        testVehicle.setSubscriptionTier(SubscriptionTier.BASIC);
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    @DisplayName("Complete vehicle onboarding and key creation workflow")
    void completeVehicleOnboardingAndKeyCreationWorkflow() {
        // Step 1: Verify vehicle is created with basic subscription
        assertEquals(SubscriptionTier.BASIC, testVehicle.getSubscriptionTier());
        assertTrue(testVehicle.getSubscriptionActive());
        assertEquals(5, testVehicle.getMaxKeysAllowed());

        // Step 2: Upgrade subscription to Premium
        Subscription subscription = subscriptionService.upgradeSubscription(testVehicle.getVin(), SubscriptionTier.PREMIUM);
        assertNotNull(subscription);
        assertEquals(SubscriptionTier.PREMIUM, subscription.getTier());

        // Refresh vehicle
        testVehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").orElseThrow();
        assertEquals(SubscriptionTier.PREMIUM, testVehicle.getSubscriptionTier());
        assertEquals(20, testVehicle.getMaxKeysAllowed());

        // Step 3: Create owner key
        TrackKeyRequest ownerKeyRequest = new TrackKeyRequest();
        ownerKeyRequest.setKeyId("OWNER-KEY-001");
        ownerKeyRequest.setDeviceId("DEVICE-001");
        ownerKeyRequest.setDeviceOem("Apple");
        ownerKeyRequest.setVehicleId(testVehicle.getVin());
        ownerKeyRequest.setKeyType("OWNER");

        TrackKeyResponse ownerKeyResponse = digitalKeyService.trackKey(ownerKeyRequest);
        assertTrue(ownerKeyResponse.isSuccess());

        // Step 4: Create friend key
        ShareKeyRequest shareRequest = new ShareKeyRequest();
        shareRequest.setVehicleVin(testVehicle.getVin());
        shareRequest.setFriendEmail("friend@example.com");
        shareRequest.setFriendName("Jane Smith");
        shareRequest.setPermissionLevel(PermissionLevel.DRIVE_ONLY);
        shareRequest.setExpiresAt(LocalDateTime.now().plusDays(30));
        shareRequest.setSharedBy("OWNER001");

        ShareKeyResponse shareResponse = keySharingService.shareKey(shareRequest);
        assertTrue(shareResponse.isSuccess());
        assertNotNull(shareResponse.getInvitationCode());

        // Step 5: Accept friend key invitation
        DigitalKey friendKey = keySharingService.acceptKeyInvitation(
            shareResponse.getInvitationCode(), "FRIEND-DEVICE-001", "Samsung");
        assertNotNull(friendKey);
        assertEquals(KeyType.FRIEND, friendKey.getKeyType());
        assertEquals(PermissionLevel.DRIVE_ONLY, friendKey.getPermissionLevel());

        // Step 6: Verify vehicle key count updated
        testVehicle = vehicleRepository.findByVin("1HGBH41JXMN109186").orElseThrow();
        assertEquals(2, testVehicle.getCurrentKeyCount());
    }

    @Test
    @DisplayName("Key lifecycle management workflow")
    void keyLifecycleManagementWorkflow() {
        // Step 1: Create a key
        TrackKeyRequest trackRequest = new TrackKeyRequest();
        trackRequest.setKeyId("LIFECYCLE-KEY-001");
        trackRequest.setDeviceId("DEVICE-002");
        trackRequest.setDeviceOem("Apple");
        trackRequest.setVehicleId(testVehicle.getVin());
        trackRequest.setKeyType("OWNER");

        TrackKeyResponse trackResponse = digitalKeyService.trackKey(trackRequest);
        assertTrue(trackResponse.isSuccess());

        // Step 2: Suspend the key
        ManageKeyRequest suspendRequest = new ManageKeyRequest();
        suspendRequest.setKeyId("LIFECYCLE-KEY-001");
        suspendRequest.setAction("SUSPEND");
        suspendRequest.setReason("Temporary suspension");
        suspendRequest.setRequestedBy("ADMIN");

        ManageKeyResponse suspendResponse = digitalKeyService.manageKey(suspendRequest);
        assertTrue(suspendResponse.isSuccess());
        assertEquals("SUSPENDED", suspendResponse.getNewStatus());

        // Step 3: Resume the key
        ManageKeyRequest resumeRequest = new ManageKeyRequest();
        resumeRequest.setKeyId("LIFECYCLE-KEY-001");
        resumeRequest.setAction("RESUME");
        resumeRequest.setReason("Issue resolved");
        resumeRequest.setRequestedBy("ADMIN");

        ManageKeyResponse resumeResponse = digitalKeyService.manageKey(resumeRequest);
        assertTrue(resumeResponse.isSuccess());
        assertEquals("ACTIVE", resumeResponse.getNewStatus());

        // Step 4: Terminate the key
        ManageKeyRequest terminateRequest = new ManageKeyRequest();
        terminateRequest.setKeyId("LIFECYCLE-KEY-001");
        terminateRequest.setAction("TERMINATE");
        terminateRequest.setReason("Security violation");
        terminateRequest.setRequestedBy("ADMIN");

        ManageKeyResponse terminateResponse = digitalKeyService.manageKey(terminateRequest);
        assertTrue(terminateResponse.isSuccess());
        assertEquals("TERMINATED", terminateResponse.getNewStatus());

        // Verify key is terminated
        DigitalKey terminatedKey = digitalKeyRepository.findByKeyId("LIFECYCLE-KEY-001").orElseThrow();
        assertEquals(KeyStatus.TERMINATED, terminatedKey.getStatus());
        assertNotNull(terminatedKey.getRevokedAt());
        assertEquals("ADMIN", terminatedKey.getRevokedBy());
        assertEquals("Security violation", terminatedKey.getRevocationReason());
    }

    @Test
    @DisplayName("Subscription management workflow")
    void subscriptionManagementWorkflow() {
        // Step 1: Start with Basic subscription
        assertEquals(SubscriptionTier.BASIC, testVehicle.getSubscriptionTier());

        // Step 2: Upgrade to Premium
        Subscription premiumSubscription = subscriptionService.upgradeSubscription(
            testVehicle.getVin(), SubscriptionTier.PREMIUM);
        assertEquals(SubscriptionTier.PREMIUM, premiumSubscription.getTier());

        // Step 3: Create multiple keys (allowed in Premium)
        for (int i = 1; i <= 10; i++) {
            TrackKeyRequest keyRequest = new TrackKeyRequest();
            keyRequest.setKeyId("BULK-KEY-" + String.format("%03d", i));
            keyRequest.setDeviceId("DEVICE-" + String.format("%03d", i));
            keyRequest.setDeviceOem("Apple");
            keyRequest.setVehicleId(testVehicle.getVin());
            keyRequest.setKeyType("OWNER");

            TrackKeyResponse response = digitalKeyService.trackKey(keyRequest);
            assertTrue(response.isSuccess());
        }

        // Step 4: Try to downgrade (should fail due to key count)
        assertThrows(Exception.class, () -> 
            subscriptionService.downgradeSubscription(testVehicle.getVin(), SubscriptionTier.BASIC));

        // Step 5: Remove some keys and then downgrade
        for (int i = 6; i <= 10; i++) {
            ManageKeyRequest terminateRequest = new ManageKeyRequest();
            terminateRequest.setKeyId("BULK-KEY-" + String.format("%03d", i));
            terminateRequest.setAction("TERMINATE");
            terminateRequest.setReason("Downgrade preparation");
            terminateRequest.setRequestedBy("OWNER001");

            digitalKeyService.manageKey(terminateRequest);
        }

        // Step 6: Now downgrade should succeed
        Subscription basicSubscription = subscriptionService.downgradeSubscription(
            testVehicle.getVin(), SubscriptionTier.BASIC);
        assertEquals(SubscriptionTier.BASIC, basicSubscription.getTier());
    }

    @Test
    @DisplayName("Vehicle pairing workflow")
    void vehiclePairingWorkflow() {
        // Step 1: Initiate pairing
        InitiatePairingRequest initiateRequest = new InitiatePairingRequest();
        initiateRequest.setVin(testVehicle.getVin());
        initiateRequest.setInitiatedBy("OWNER001");

        InitiatePairingResponse initiateResponse = vehiclePairingService.initiatePairing(initiateRequest);
        assertTrue(initiateResponse.isSuccess());
        assertNotNull(initiateResponse.getSessionId());
        assertNotNull(initiateResponse.getPairingPassword());
        assertNotNull(initiateResponse.getVehiclePublicKey());

        // Step 2: Complete pairing
        CompletePairingRequest completeRequest = new CompletePairingRequest();
        completeRequest.setSessionId(initiateResponse.getSessionId());
        completeRequest.setPairingPassword(initiateResponse.getPairingPassword());
        completeRequest.setDeviceId("PAIRED-DEVICE-001");
        completeRequest.setDeviceOem("Apple");
        completeRequest.setDevicePublicKey("device-public-key-base64");

        CompletePairingResponse completeResponse = vehiclePairingService.completePairing(completeRequest);
        assertTrue(completeResponse.isSuccess());
        assertNotNull(completeResponse.getVehiclePublicKey());
    }

    @Test
    @DisplayName("Friend key sharing and management workflow")
    void friendKeySharingAndManagementWorkflow() {
        // Step 1: Upgrade to Premium to allow friend keys
        subscriptionService.upgradeSubscription(testVehicle.getVin(), SubscriptionTier.PREMIUM);

        // Step 2: Share key with friend
        ShareKeyRequest shareRequest = new ShareKeyRequest();
        shareRequest.setVehicleVin(testVehicle.getVin());
        shareRequest.setFriendEmail("friend@example.com");
        shareRequest.setFriendName("Friend User");
        shareRequest.setPermissionLevel(PermissionLevel.DRIVE_ONLY);
        shareRequest.setMaxUsageCount(50L);
        shareRequest.setSharedBy("OWNER001");

        ShareKeyResponse shareResponse = keySharingService.shareKey(shareRequest);
        assertTrue(shareResponse.isSuccess());

        // Step 3: Accept invitation
        DigitalKey friendKey = keySharingService.acceptKeyInvitation(
            shareResponse.getInvitationCode(), "FRIEND-DEVICE", "Samsung");
        assertNotNull(friendKey);

        // Step 4: Update permissions
        keySharingService.updateKeyPermissions(
            friendKey.getKeyId(), PermissionLevel.UNLOCK_ONLY, "OWNER001");

        // Step 5: Set restrictions
        keySharingService.setKeyRestrictions(
            friendKey.getKeyId(), 
            "{\"weekdays\": \"9-17\"}", 
            "{\"radius\": 50}", 
            25L, 
            "OWNER001");

        // Step 6: Revoke friend key
        keySharingService.revokeSharedKey(
            friendKey.getKeyId(), "OWNER001", "No longer needed");

        // Verify key is revoked
        DigitalKey revokedKey = digitalKeyRepository.findByKeyId(friendKey.getKeyId()).orElseThrow();
        assertEquals(KeyStatus.TERMINATED, revokedKey.getStatus());
        assertTrue(revokedKey.isRevoked());
    }

    @Test
    @DisplayName("Error handling and validation workflow")
    void errorHandlingAndValidationWorkflow() {
        // Test invalid VIN
        TrackKeyRequest invalidVinRequest = new TrackKeyRequest();
        invalidVinRequest.setKeyId("INVALID-VIN-KEY");
        invalidVinRequest.setDeviceId("DEVICE-001");
        invalidVinRequest.setDeviceOem("Apple");
        invalidVinRequest.setVehicleId("INVALID-VIN");
        invalidVinRequest.setKeyType("OWNER");

        assertThrows(Exception.class, () -> digitalKeyService.trackKey(invalidVinRequest));

        // Test friend key without Premium subscription
        ShareKeyRequest basicTierShareRequest = new ShareKeyRequest();
        basicTierShareRequest.setVehicleVin(testVehicle.getVin());
        basicTierShareRequest.setFriendEmail("friend@example.com");
        basicTierShareRequest.setSharedBy("OWNER001");

        assertThrows(Exception.class, () -> keySharingService.shareKey(basicTierShareRequest));

        // Test managing non-existent key
        ManageKeyRequest nonExistentKeyRequest = new ManageKeyRequest();
        nonExistentKeyRequest.setKeyId("NON-EXISTENT-KEY");
        nonExistentKeyRequest.setAction("SUSPEND");
        nonExistentKeyRequest.setRequestedBy("ADMIN");

        assertThrows(Exception.class, () -> digitalKeyService.manageKey(nonExistentKeyRequest));
    }
}
