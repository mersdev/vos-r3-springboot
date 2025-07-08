package com.vehicleoem.demo;

import com.vehicleoem.security.CryptographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Base64;

/**
 * Demonstration class showing real-world ECIES encryption/decryption usage
 * This demonstrates the practical application of ECIES in vehicle-to-OEM communication
 */
@Component
public class ECIESDemo implements CommandLineRunner {

    @Autowired
    private CryptographyService cryptographyService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "demo".equals(args[0])) {
            demonstrateECIES();
        }
    }

    public void demonstrateECIES() {
        System.out.println("=== ECIES (Elliptic Curve Integrated Encryption Scheme) Demo ===\n");

        try {
            // 1. Generate key pairs for vehicle and OEM
            System.out.println("1. Generating EC key pairs...");
            KeyPair vehicleKeyPair = cryptographyService.generateECKeyPair();
            KeyPair oemKeyPair = cryptographyService.generateECKeyPair();
            
            System.out.println("   ✓ Vehicle key pair generated");
            System.out.println("   ✓ OEM key pair generated");
            System.out.println();

            // 2. Demonstrate vehicle sending encrypted data to OEM
            System.out.println("2. Vehicle encrypting sensitive data for OEM...");
            String sensitiveVehicleData = """
                {
                    "vehicleId": "VIN123456789",
                    "location": {
                        "latitude": 37.7749,
                        "longitude": -122.4194
                    },
                    "diagnostics": {
                        "engineTemp": 95.5,
                        "batteryLevel": 87.3,
                        "mileage": 45678
                    },
                    "timestamp": "2025-07-08T10:30:00Z"
                }
                """;

            String encryptedData = cryptographyService.encryptWithECIES(
                sensitiveVehicleData, 
                oemKeyPair.getPublic()
            );
            
            System.out.println("   ✓ Data encrypted using OEM's public key");
            System.out.println("   Encrypted data length: " + encryptedData.length() + " characters");
            System.out.println("   Encrypted data (first 100 chars): " + 
                encryptedData.substring(0, Math.min(100, encryptedData.length())) + "...");
            System.out.println();

            // 3. Demonstrate OEM decrypting the data
            System.out.println("3. OEM decrypting received data...");
            String decryptedData = cryptographyService.decryptWithECIES(
                encryptedData, 
                oemKeyPair.getPrivate()
            );
            
            System.out.println("   ✓ Data successfully decrypted using OEM's private key");
            System.out.println("   Decrypted data matches original: " + 
                sensitiveVehicleData.equals(decryptedData));
            System.out.println();

            // 4. Demonstrate OEM sending encrypted commands to vehicle
            System.out.println("4. OEM encrypting command for vehicle...");
            String oemCommand = """
                {
                    "command": "UPDATE_SOFTWARE",
                    "version": "v2.1.3",
                    "downloadUrl": "https://oem.example.com/updates/v2.1.3",
                    "checksum": "sha256:abc123def456",
                    "priority": "HIGH",
                    "scheduledTime": "2025-07-08T23:00:00Z"
                }
                """;

            String encryptedCommand = cryptographyService.encryptWithECIES(
                oemCommand, 
                vehicleKeyPair.getPublic()
            );
            
            System.out.println("   ✓ Command encrypted using vehicle's public key");
            System.out.println("   Encrypted command length: " + encryptedCommand.length() + " characters");
            System.out.println();

            // 5. Demonstrate vehicle decrypting the command
            System.out.println("5. Vehicle decrypting received command...");
            String decryptedCommand = cryptographyService.decryptWithECIES(
                encryptedCommand, 
                vehicleKeyPair.getPrivate()
            );
            
            System.out.println("   ✓ Command successfully decrypted using vehicle's private key");
            System.out.println("   Command matches original: " + 
                oemCommand.equals(decryptedCommand));
            System.out.println();

            // 6. Demonstrate key validation
            System.out.println("6. Validating key pairs for ECIES compatibility...");
            boolean vehicleKeysValid = cryptographyService.validateECIESKeyPair(vehicleKeyPair);
            boolean oemKeysValid = cryptographyService.validateECIESKeyPair(oemKeyPair);
            
            System.out.println("   Vehicle key pair valid: " + vehicleKeysValid);
            System.out.println("   OEM key pair valid: " + oemKeysValid);
            System.out.println();

            // 7. Demonstrate key information
            System.out.println("7. Key information:");
            System.out.println("   Vehicle public key (Base64): " + 
                Base64.getEncoder().encodeToString(vehicleKeyPair.getPublic().getEncoded()));
            System.out.println("   OEM public key (Base64): " + 
                Base64.getEncoder().encodeToString(oemKeyPair.getPublic().getEncoded()));
            System.out.println();

            // 8. Demonstrate security features
            System.out.println("8. ECIES Security Features:");
            System.out.println("   ✓ Elliptic Curve Diffie-Hellman (ECDH) for key agreement");
            System.out.println("   ✓ KDF2 with SHA-256 for key derivation");
            System.out.println("   ✓ AES-128 for symmetric encryption");
            System.out.println("   ✓ HMAC-SHA256 for message authentication");
            System.out.println("   ✓ secp256r1 (NIST P-256) elliptic curve");
            System.out.println("   ✓ Forward secrecy through ephemeral keys");
            System.out.println();

            System.out.println("=== ECIES Demo Completed Successfully ===");

        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
