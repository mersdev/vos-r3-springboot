package com.vehicleoem.model;

public enum SubscriptionTier {
    BASIC(5, 30, false, false),
    PREMIUM(20, 90, true, false),
    ENTERPRISE(100, 365, true, true);
    
    private final int maxKeys;
    private final int keyExpirationDays;
    private final boolean friendKeysAllowed;
    private final boolean analyticsEnabled;
    
    SubscriptionTier(int maxKeys, int keyExpirationDays, boolean friendKeysAllowed, boolean analyticsEnabled) {
        this.maxKeys = maxKeys;
        this.keyExpirationDays = keyExpirationDays;
        this.friendKeysAllowed = friendKeysAllowed;
        this.analyticsEnabled = analyticsEnabled;
    }
    
    public int getMaxKeys() { return maxKeys; }
    public int getKeyExpirationDays() { return keyExpirationDays; }
    public boolean isFriendKeysAllowed() { return friendKeysAllowed; }
    public boolean isAnalyticsEnabled() { return analyticsEnabled; }
}
