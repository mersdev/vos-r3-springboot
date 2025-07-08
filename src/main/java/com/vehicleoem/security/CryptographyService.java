package com.vehicleoem.security;

import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class CryptographyService {
    
    private static final String CURVE_NAME = "secp256r1"; // NIST P-256
    private static final String ECIES_ALGORITHM = "ECIES";

    // ECIES parameters for secure encryption
    private static final byte[] DERIVATION_VECTOR = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final byte[] ENCODING_VECTOR = new byte[]{8, 7, 6, 5, 4, 3, 2, 1};
    private static final int MAC_KEY_SIZE = 128; // bits
    private static final int CIPHER_KEY_SIZE = 128; // bits

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public String generatePairingPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    public String generatePairingVerifier(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty");
        }
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
        if (data == null || data.length == 0) {
            throw new RuntimeException("Data cannot be null or empty");
        }
        if (privateKey == null) {
            throw new RuntimeException("Private key cannot be null");
        }
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
    
    /**
     * Encrypts data using ECIES (Elliptic Curve Integrated Encryption Scheme)
     * This is a hybrid encryption scheme that combines:
     * - ECDH for key agreement
     * - KDF2 for key derivation
     * - AES for symmetric encryption
     * - HMAC-SHA256 for authentication
     *
     * @param data The plaintext data to encrypt
     * @param publicKey The recipient's EC public key
     * @return Base64-encoded encrypted data
     */
    public String encryptWithECIES(String data, PublicKey publicKey) {
        if (data == null) {
            throw new RuntimeException("Data cannot be null");
        }
        if (publicKey == null) {
            throw new RuntimeException("Public key cannot be null");
        }
        if (!(publicKey instanceof ECPublicKey)) {
            throw new RuntimeException("Public key must be an EC public key");
        }

        try {
            // Create ECIES cipher with BouncyCastle provider
            Cipher cipher = Cipher.getInstance(ECIES_ALGORITHM, "BC");

            // Configure ECIES parameters for secure encryption
            // IESParameterSpec(derivation, encoding, macKeySize, cipherKeySize, nonce, usePointCompression)
            IESParameterSpec iesParams = new IESParameterSpec(
                DERIVATION_VECTOR,  // derivation vector for KDF2
                ENCODING_VECTOR,    // encoding vector for MAC
                MAC_KEY_SIZE,       // MAC key size in bits
                CIPHER_KEY_SIZE,    // cipher key size in bits
                null,               // nonce (null for random)
                false               // use point compression
            );

            // Initialize cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, iesParams);

            // Encrypt the data
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

            // Return Base64-encoded result
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data with ECIES: " + e.getMessage(), e);
        }
    }
    
    /**
     * Decrypts data using ECIES (Elliptic Curve Integrated Encryption Scheme)
     * This method reverses the encryption process:
     * - Derives the shared secret using ECDH
     * - Uses KDF2 to derive encryption and MAC keys
     * - Verifies the HMAC for authentication
     * - Decrypts the data using AES
     *
     * @param encryptedData Base64-encoded encrypted data
     * @param privateKey The recipient's EC private key
     * @return The decrypted plaintext data
     */
    public String decryptWithECIES(String encryptedData, PrivateKey privateKey) {
        if (encryptedData == null) {
            throw new RuntimeException("Encrypted data cannot be null");
        }
        if (privateKey == null) {
            throw new RuntimeException("Private key cannot be null");
        }
        if (!(privateKey instanceof ECPrivateKey)) {
            throw new RuntimeException("Private key must be an EC private key");
        }

        try {
            // Create ECIES cipher with BouncyCastle provider
            Cipher cipher = Cipher.getInstance(ECIES_ALGORITHM, "BC");

            // Configure ECIES parameters (must match encryption parameters)
            // IESParameterSpec(derivation, encoding, macKeySize, cipherKeySize, nonce, usePointCompression)
            IESParameterSpec iesParams = new IESParameterSpec(
                DERIVATION_VECTOR,  // derivation vector for KDF2
                ENCODING_VECTOR,    // encoding vector for MAC
                MAC_KEY_SIZE,       // MAC key size in bits
                CIPHER_KEY_SIZE,    // cipher key size in bits
                null,               // nonce (null for random)
                false               // use point compression
            );

            // Initialize cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, privateKey, iesParams);

            // Decode and decrypt the data
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Return the decrypted string
            return new String(decryptedBytes, "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data with ECIES: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a symmetric AES key for use in hybrid encryption schemes
     *
     * @return A 256-bit AES SecretKey
     */
    public SecretKey generateSymmetricKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 256-bit AES key
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate symmetric key", e);
        }
    }

    /**
     * Validates that the provided key pair is compatible with ECIES encryption
     *
     * @param keyPair The EC key pair to validate
     * @return true if the key pair is valid for ECIES operations
     */
    public boolean validateECIESKeyPair(KeyPair keyPair) {
        if (keyPair == null) {
            return false;
        }

        try {
            // Test encryption/decryption with a small test message
            String testMessage = "ECIES_TEST_MESSAGE";
            String encrypted = encryptWithECIES(testMessage, keyPair.getPublic());
            String decrypted = decryptWithECIES(encrypted, keyPair.getPrivate());

            return testMessage.equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }
}
