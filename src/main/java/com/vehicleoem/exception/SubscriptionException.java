package com.vehicleoem.exception;

public class SubscriptionException extends BusinessException {
    public SubscriptionException(String message) {
        super("SUBSCRIPTION_ERROR", message);
    }
    
    public static SubscriptionException expired() {
        return new SubscriptionException("Vehicle subscription has expired");
    }
    
    public static SubscriptionException inactive() {
        return new SubscriptionException("Vehicle subscription is not active");
    }
    
    public static SubscriptionException keyLimitReached(int maxKeys) {
        return new SubscriptionException("Maximum number of keys reached: " + maxKeys);
    }
    
    public static SubscriptionException friendKeysNotAllowed() {
        return new SubscriptionException("Friend keys are not allowed for this subscription tier");
    }
}
