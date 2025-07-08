package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tier")
    private SubscriptionTier tier;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    private BillingCycle billingCycle = BillingCycle.MONTHLY;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "monthly_price", precision = 10, scale = 2)
    private BigDecimal monthlyPrice;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;
    
    @Column(name = "auto_renew")
    private Boolean autoRenew = true;
    
    @Column(name = "grace_period_ends_at")
    private LocalDateTime gracePeriodEndsAt;
    
    @Column(name = "cancellation_requested_at")
    private LocalDateTime cancellationRequestedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;
    
    @Column(name = "promo_code", length = 50)
    private String promoCode;
    
    @Column(name = "discount_percentage")
    private Integer discountPercentage = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Subscription() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Subscription(Vehicle vehicle, SubscriptionTier tier, BillingCycle billingCycle) {
        this();
        this.vehicle = vehicle;
        this.tier = tier;
        this.billingCycle = billingCycle;
        this.monthlyPrice = calculatePrice(tier, billingCycle);
        this.startedAt = LocalDateTime.now();
        this.expiresAt = calculateExpirationDate(billingCycle);
        this.nextBillingDate = calculateNextBillingDate(billingCycle);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public SubscriptionTier getTier() { return tier; }
    public void setTier(SubscriptionTier tier) { this.tier = tier; }
    
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    
    public BillingCycle getBillingCycle() { return billingCycle; }
    public void setBillingCycle(BillingCycle billingCycle) { this.billingCycle = billingCycle; }
    
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    
    public LocalDateTime getLastBillingDate() { return lastBillingDate; }
    public void setLastBillingDate(LocalDateTime lastBillingDate) { this.lastBillingDate = lastBillingDate; }
    
    public Boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }
    
    public LocalDateTime getGracePeriodEndsAt() { return gracePeriodEndsAt; }
    public void setGracePeriodEndsAt(LocalDateTime gracePeriodEndsAt) { this.gracePeriodEndsAt = gracePeriodEndsAt; }
    
    public LocalDateTime getCancellationRequestedAt() { return cancellationRequestedAt; }
    public void setCancellationRequestedAt(LocalDateTime cancellationRequestedAt) { this.cancellationRequestedAt = cancellationRequestedAt; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    
    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }
    
    public boolean isInGracePeriod() {
        return status == SubscriptionStatus.GRACE_PERIOD && 
               gracePeriodEndsAt != null && gracePeriodEndsAt.isAfter(LocalDateTime.now());
    }
    
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now()) && !isInGracePeriod();
    }
    
    public BigDecimal calculateEffectivePrice() {
        BigDecimal price = monthlyPrice;
        if (discountPercentage > 0) {
            BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100));
            price = price.subtract(discount);
        }
        return price;
    }
    
    public void renew() {
        this.status = SubscriptionStatus.ACTIVE;
        this.lastBillingDate = LocalDateTime.now();
        this.nextBillingDate = calculateNextBillingDate(billingCycle);
        this.expiresAt = calculateExpirationDate(billingCycle);
        this.gracePeriodEndsAt = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void enterGracePeriod() {
        this.status = SubscriptionStatus.GRACE_PERIOD;
        this.gracePeriodEndsAt = LocalDateTime.now().plusDays(7); // 7-day grace period
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.autoRenew = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    private BigDecimal calculatePrice(SubscriptionTier tier, BillingCycle cycle) {
        BigDecimal basePrice;
        switch (tier) {
            case BASIC: basePrice = BigDecimal.valueOf(9.99); break;
            case PREMIUM: basePrice = BigDecimal.valueOf(19.99); break;
            case ENTERPRISE: basePrice = BigDecimal.valueOf(49.99); break;
            default: basePrice = BigDecimal.valueOf(9.99);
        }
        
        // Apply billing cycle discounts
        if (cycle == BillingCycle.YEARLY) {
            return basePrice.multiply(BigDecimal.valueOf(10)); // 2 months free for yearly
        }
        return basePrice;
    }
    
    private LocalDateTime calculateExpirationDate(BillingCycle cycle) {
        return cycle == BillingCycle.YEARLY ? 
            LocalDateTime.now().plusYears(1) : LocalDateTime.now().plusMonths(1);
    }
    
    private LocalDateTime calculateNextBillingDate(BillingCycle cycle) {
        return cycle == BillingCycle.YEARLY ? 
            LocalDateTime.now().plusYears(1) : LocalDateTime.now().plusMonths(1);
    }
    
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
