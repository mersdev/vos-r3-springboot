package com.vehicleoem.exception;

public class KeyManagementException extends BusinessException {
    public KeyManagementException(String message) {
        super("KEY_MANAGEMENT_ERROR", message);
    }
    
    public static KeyManagementException keyNotFound(String keyId) {
        return new KeyManagementException("Digital key not found: " + keyId);
    }
    
    public static KeyManagementException keyAlreadyExists(String keyId) {
        return new KeyManagementException("Digital key already exists: " + keyId);
    }
    
    public static KeyManagementException invalidKeyStatus(String keyId, String currentStatus, String action) {
        return new KeyManagementException("Cannot perform action '" + action + "' on key " + keyId + " with status " + currentStatus);
    }
    
    public static KeyManagementException keyExpired(String keyId) {
        return new KeyManagementException("Digital key has expired: " + keyId);
    }
    
    public static KeyManagementException usageLimitReached(String keyId) {
        return new KeyManagementException("Usage limit reached for key: " + keyId);
    }
}
