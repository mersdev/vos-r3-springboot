package com.vehicleoem.model;

public enum BillingCycle {
    MONTHLY("Monthly billing"),
    YEARLY("Yearly billing with discount");
    
    private final String description;
    
    BillingCycle(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
