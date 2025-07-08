package com.vehicleoem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CryptographyService Unit Tests")
class CryptographyServiceTest {

    @InjectMocks
    private CryptographyService cryptographyService;

    @BeforeEach
    void setUp() {
        // Initialize the service
    }

    @Test
    @DisplayName("Should generate EC key pair")
    void shouldGenerateEcKeyPair() {
        // Act
        KeyPair keyPair = cryptographyService.generateECKeyPair();

        // Assert
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublic());
        assertNotNull(keyPair.getPrivate());
        assertEquals("EC", keyPair.getPublic().getAlgorithm());
        assertEquals("EC", keyPair.getPrivate().getAlgorithm());
    }

    @Test
    @DisplayName("Should generate different key pairs")
    void shouldGenerateDifferentKeyPairs() {
        // Act
        KeyPair keyPair1 = cryptographyService.generateECKeyPair();
        KeyPair keyPair2 = cryptographyService.generateECKeyPair();

        // Assert
        assertNotEquals(keyPair1.getPublic(), keyPair2.getPublic());
        assertNotEquals(keyPair1.getPrivate(), keyPair2.getPrivate());
    }

    @Test
    @DisplayName("Should sign and verify data")
    void shouldSignAndVerifyData() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String testData = "Test data to sign";
        byte[] testDataBytes = testData.getBytes();

        // Act
        String signature = cryptographyService.signData(testDataBytes, keyPair.getPrivate());
        boolean isValid = cryptographyService.verifySignature(testDataBytes, signature, keyPair.getPublic());

        // Assert
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should fail verification with wrong public key")
    void shouldFailVerificationWithWrongPublicKey() {
        // Arrange
        KeyPair keyPair1 = cryptographyService.generateECKeyPair();
        KeyPair keyPair2 = cryptographyService.generateECKeyPair();
        String testData = "Test data to sign";
        byte[] testDataBytes = testData.getBytes();

        // Act
        String signature = cryptographyService.signData(testDataBytes, keyPair1.getPrivate());
        boolean isValid = cryptographyService.verifySignature(testDataBytes, signature, keyPair2.getPublic());

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should fail verification with tampered data")
    void shouldFailVerificationWithTamperedData() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String originalData = "Original data";
        String tamperedData = "Tampered data";
        byte[] originalDataBytes = originalData.getBytes();
        byte[] tamperedDataBytes = tamperedData.getBytes();

        // Act
        String signature = cryptographyService.signData(originalDataBytes, keyPair.getPrivate());
        boolean isValid = cryptographyService.verifySignature(tamperedDataBytes, signature, keyPair.getPublic());

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should encrypt and decrypt data")
    void shouldEncryptAndDecryptData() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String plaintext = "Secret message to encrypt";

        // Act
        String encryptedData = cryptographyService.encryptWithECIES(plaintext, keyPair.getPublic());
        String decryptedData = cryptographyService.decryptWithECIES(encryptedData, keyPair.getPrivate());

        // Assert
        assertNotNull(encryptedData);
        assertNotEquals(plaintext, encryptedData);
        assertEquals(plaintext, decryptedData);
    }

    @Test
    @DisplayName("Should generate pairing password")
    void shouldGeneratePairingPassword() {
        // Act
        String password1 = cryptographyService.generatePairingPassword();
        String password2 = cryptographyService.generatePairingPassword();

        // Assert
        assertNotNull(password1);
        assertNotNull(password2);
        assertNotEquals(password1, password2);
        assertTrue(password1.length() >= 8);
        assertTrue(password2.length() >= 8);
    }

    @Test
    @DisplayName("Should generate pairing verifier")
    void shouldGeneratePairingVerifier() {
        // Arrange
        String password = "test-password-123";

        // Act
        String verifier1 = cryptographyService.generatePairingVerifier(password);
        String verifier2 = cryptographyService.generatePairingVerifier(password);

        // Assert
        assertNotNull(verifier1);
        assertNotNull(verifier2);
        assertEquals(verifier1, verifier2); // Same password should generate same verifier
        assertNotEquals(password, verifier1); // Verifier should be different from password
    }

    @Test
    @DisplayName("Should generate different verifiers for different passwords")
    void shouldGenerateDifferentVerifiersForDifferentPasswords() {
        // Arrange
        String password1 = "password1";
        String password2 = "password2";

        // Act
        String verifier1 = cryptographyService.generatePairingVerifier(password1);
        String verifier2 = cryptographyService.generatePairingVerifier(password2);

        // Assert
        assertNotEquals(verifier1, verifier2);
    }

    @Test
    @DisplayName("Should handle null and empty inputs gracefully")
    void shouldHandleNullAndEmptyInputsGracefully() {
        // Test null data signing
        KeyPair keyPair = cryptographyService.generateECKeyPair();

        assertThrows(RuntimeException.class, () ->
            cryptographyService.signData(null, keyPair.getPrivate()));

        assertThrows(RuntimeException.class, () ->
            cryptographyService.signData(new byte[0], keyPair.getPrivate()));

        // Test null key signing
        assertThrows(RuntimeException.class, () ->
            cryptographyService.signData("test".getBytes(), null));

        // Test null data encryption
        assertThrows(RuntimeException.class, () ->
            cryptographyService.encryptWithECIES(null, keyPair.getPublic()));

        // Test null pairing password
        assertThrows(RuntimeException.class, () ->
            cryptographyService.generatePairingVerifier(null));

        assertThrows(RuntimeException.class, () ->
            cryptographyService.generatePairingVerifier(""));
    }

    @Test
    @DisplayName("Should handle invalid signature format")
    void shouldHandleInvalidSignatureFormat() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String testData = "Test data";
        byte[] testDataBytes = testData.getBytes();
        String invalidSignature = "invalid-signature-format";

        // Act & Assert
        assertFalse(cryptographyService.verifySignature(testDataBytes, invalidSignature, keyPair.getPublic()));
    }

    @Test
    @DisplayName("Should handle invalid encrypted data format")
    void shouldHandleInvalidEncryptedDataFormat() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String invalidEncryptedData = "invalid-encrypted-data";

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            cryptographyService.decryptWithECIES(invalidEncryptedData, keyPair.getPrivate()));
    }

    @Test
    @DisplayName("Should generate consistent key formats")
    void shouldGenerateConsistentKeyFormats() {
        // Act
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        
        // Convert to Base64 and back
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // Assert
        assertNotNull(publicKeyBase64);
        assertNotNull(privateKeyBase64);
        assertTrue(publicKeyBase64.length() > 0);
        assertTrue(privateKeyBase64.length() > 0);
        
        // Verify Base64 format
        assertDoesNotThrow(() -> Base64.getDecoder().decode(publicKeyBase64));
        assertDoesNotThrow(() -> Base64.getDecoder().decode(privateKeyBase64));
    }

    @Test
    @DisplayName("Should handle large data encryption")
    void shouldHandleLargeDataEncryption() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 100; i++) { // Reduced size for test
            largeData.append("This is a large piece of data for testing encryption. ");
        }
        String plaintext = largeData.toString();

        // Act
        String encryptedData = cryptographyService.encryptWithECIES(plaintext, keyPair.getPublic());
        String decryptedData = cryptographyService.decryptWithECIES(encryptedData, keyPair.getPrivate());

        // Assert
        assertEquals(plaintext, decryptedData);
    }

    @Test
    @DisplayName("Should validate ECIES key pair")
    void shouldValidateECIESKeyPair() {
        // Arrange
        KeyPair validKeyPair = cryptographyService.generateECKeyPair();

        // Act
        boolean isValid = cryptographyService.validateECIESKeyPair(validKeyPair);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject null key pair validation")
    void shouldRejectNullKeyPairValidation() {
        // Act
        boolean isValid = cryptographyService.validateECIESKeyPair(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate symmetric key")
    void shouldGenerateSymmetricKey() {
        // Act
        var symmetricKey = cryptographyService.generateSymmetricKey();

        // Assert
        assertNotNull(symmetricKey);
        assertEquals("AES", symmetricKey.getAlgorithm());
        assertEquals(32, symmetricKey.getEncoded().length); // 256 bits = 32 bytes
    }

    @Test
    @DisplayName("Should generate different symmetric keys")
    void shouldGenerateDifferentSymmetricKeys() {
        // Act
        var key1 = cryptographyService.generateSymmetricKey();
        var key2 = cryptographyService.generateSymmetricKey();

        // Assert
        assertNotEquals(key1, key2);
        assertFalse(java.util.Arrays.equals(key1.getEncoded(), key2.getEncoded()));
    }

    @Test
    @DisplayName("Should handle ECIES encryption with special characters")
    void shouldHandleECIESEncryptionWithSpecialCharacters() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String specialData = "Special chars: àáâãäåæçèéêë ñòóôõö ùúûüý 中文 🚗🔐";

        // Act
        String encryptedData = cryptographyService.encryptWithECIES(specialData, keyPair.getPublic());
        String decryptedData = cryptographyService.decryptWithECIES(encryptedData, keyPair.getPrivate());

        // Assert
        assertEquals(specialData, decryptedData);
    }

    @Test
    @DisplayName("Should handle ECIES encryption with empty string")
    void shouldHandleECIESEncryptionWithEmptyString() {
        // Arrange
        KeyPair keyPair = cryptographyService.generateECKeyPair();
        String emptyData = "";

        // Act
        String encryptedData = cryptographyService.encryptWithECIES(emptyData, keyPair.getPublic());
        String decryptedData = cryptographyService.decryptWithECIES(encryptedData, keyPair.getPrivate());

        // Assert
        assertEquals(emptyData, decryptedData);
    }

    @Test
    @DisplayName("Should fail ECIES encryption with wrong key type")
    void shouldFailECIESEncryptionWithWrongKeyType() {
        // Arrange
        try {
            var keyGen = java.security.KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            var rsaKeyPair = keyGen.generateKeyPair();
            String testData = "Test data";

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                cryptographyService.encryptWithECIES(testData, rsaKeyPair.getPublic()));
        } catch (Exception e) {
            fail("Failed to generate RSA key pair for test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail ECIES decryption with wrong key type")
    void shouldFailECIESDecryptionWithWrongKeyType() {
        // Arrange
        try {
            var keyGen = java.security.KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            var rsaKeyPair = keyGen.generateKeyPair();
            String testData = "dGVzdCBkYXRh"; // Base64 encoded "test data"

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                cryptographyService.decryptWithECIES(testData, rsaKeyPair.getPrivate()));
        } catch (Exception e) {
            fail("Failed to generate RSA key pair for test: " + e.getMessage());
        }
    }
}
