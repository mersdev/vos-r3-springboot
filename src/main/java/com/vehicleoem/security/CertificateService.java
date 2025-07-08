package com.vehicleoem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Base64;

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
            return String.format("OriginalCert:%s|CrossSignedBy:%s|CrossSignedAt:%s|CrossSignature:%s", 
                originalCertificate, crossSignedBy, crossSignedAt, crossSignature);
        }
    }
}
