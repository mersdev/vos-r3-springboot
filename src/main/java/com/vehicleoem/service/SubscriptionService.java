package com.vehicleoem.service;

import com.vehicleoem.model.*;
import com.vehicleoem.repository.SubscriptionRepository;
import com.vehicleoem.repository.VehicleRepository;
import com.vehicleoem.exception.SubscriptionException;
import com.vehicleoem.exception.VehicleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
@Transactional
public class SubscriptionService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private NotificationService notificationService;
    
    public Subscription createSubscription(String vin, SubscriptionTier tier, BillingCycle billingCycle) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        // Check if subscription already exists
        if (subscriptionRepository.findByVehicle(vehicle).isPresent()) {
            throw new SubscriptionException("Vehicle already has an active subscription");
        }
        
        Subscription subscription = new Subscription(vehicle, tier, billingCycle);
        subscription = subscriptionRepository.save(subscription);
        
        // Update vehicle with subscription details
        vehicle.setSubscriptionTier(tier);
        vehicle.setSubscriptionActive(true);
        vehicle.setSubscriptionExpiresAt(subscription.getExpiresAt());
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_CREATED", "SYSTEM", 
            "Subscription created: " + tier + " - " + billingCycle);
        
        return subscription;
    }
    
    public Subscription upgradeSubscription(String vin, SubscriptionTier newTier) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No active subscription found for vehicle"));
        
        if (!subscription.isActive()) {
            throw new SubscriptionException("Cannot upgrade inactive subscription");
        }
        
        SubscriptionTier oldTier = subscription.getTier();
        subscription.setTier(newTier);
        subscription.setMonthlyPrice(calculatePrice(newTier, subscription.getBillingCycle()));
        subscription = subscriptionRepository.save(subscription);
        
        // Update vehicle
        vehicle.setSubscriptionTier(newTier);
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_UPGRADED", "SYSTEM", 
            "Subscription upgraded from " + oldTier + " to " + newTier);
        
        // Send notification
        notificationService.sendSubscriptionUpgradeNotification(vehicle.getOwner(), oldTier, newTier);
        
        return subscription;
    }
    
    public Subscription downgradeSubscription(String vin, SubscriptionTier newTier) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No active subscription found for vehicle"));
        
        if (!subscription.isActive()) {
            throw new SubscriptionException("Cannot downgrade inactive subscription");
        }
        
        // Check if downgrade is allowed based on current key count
        int currentKeyCount = vehicle.getCurrentKeyCount();
        int newMaxKeys = newTier.getMaxKeys();
        
        if (currentKeyCount > newMaxKeys) {
            throw new SubscriptionException("Cannot downgrade: Vehicle has " + currentKeyCount + 
                " keys but new tier only allows " + newMaxKeys + " keys");
        }
        
        SubscriptionTier oldTier = subscription.getTier();
        subscription.setTier(newTier);
        subscription.setMonthlyPrice(calculatePrice(newTier, subscription.getBillingCycle()));
        subscription = subscriptionRepository.save(subscription);
        
        // Update vehicle
        vehicle.setSubscriptionTier(newTier);
        vehicle.setMaxKeysAllowed(newMaxKeys);
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_DOWNGRADED", "SYSTEM", 
            "Subscription downgraded from " + oldTier + " to " + newTier);
        
        return subscription;
    }
    
    public void cancelSubscription(String vin, String reason) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No active subscription found for vehicle"));
        
        subscription.cancel(reason);
        subscriptionRepository.save(subscription);
        
        // Update vehicle
        vehicle.setSubscriptionActive(false);
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_CANCELLED", "SYSTEM", 
            "Subscription cancelled: " + reason);
        
        // Send notification
        notificationService.sendSubscriptionCancellationNotification(vehicle.getOwner(), reason);
    }
    
    public void suspendSubscription(String vin, String reason) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No active subscription found for vehicle"));
        
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        subscriptionRepository.save(subscription);
        
        // Update vehicle
        vehicle.setSubscriptionActive(false);
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_SUSPENDED", "SYSTEM", 
            "Subscription suspended: " + reason);
    }
    
    public void reactivateSubscription(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No subscription found for vehicle"));
        
        if (subscription.getStatus() != SubscriptionStatus.SUSPENDED) {
            throw new SubscriptionException("Can only reactivate suspended subscriptions");
        }
        
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);
        
        // Update vehicle
        vehicle.setSubscriptionActive(true);
        vehicleRepository.save(vehicle);
        
        // Log audit trail
        auditService.logVehicleAction(vin, "SUBSCRIPTION_REACTIVATED", "SYSTEM", 
            "Subscription reactivated");
    }
    
    public boolean processPayment(String vin, BigDecimal amount) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
            .orElseThrow(() -> new VehicleNotFoundException(vin));
        
        Subscription subscription = subscriptionRepository.findByVehicle(vehicle)
            .orElseThrow(() -> new SubscriptionException("No subscription found for vehicle"));
        
        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentProcessing(amount);
        
        if (paymentSuccessful) {
            subscription.renew();
            subscriptionRepository.save(subscription);
            
            // Update vehicle
            vehicle.setSubscriptionActive(true);
            vehicle.setSubscriptionExpiresAt(subscription.getExpiresAt());
            vehicleRepository.save(vehicle);
            
            // Log audit trail
            auditService.logVehicleAction(vin, "PAYMENT_PROCESSED", "SYSTEM", 
                "Payment processed: $" + amount);
            
            return true;
        } else {
            // Handle payment failure
            subscription.enterGracePeriod();
            subscriptionRepository.save(subscription);
            
            // Log audit trail
            auditService.logVehicleAction(vin, "PAYMENT_FAILED", "SYSTEM", 
                "Payment failed: $" + amount);
            
            // Send notification
            notificationService.sendPaymentFailureNotification(vehicle.getOwner(), amount);
            
            return false;
        }
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void processSubscriptionRenewals() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Subscription> subscriptionsToRenew = subscriptionRepository.findByNextBillingDateBefore(tomorrow);
        
        for (Subscription subscription : subscriptionsToRenew) {
            if (subscription.getAutoRenew() && subscription.isActive()) {
                BigDecimal amount = subscription.calculateEffectivePrice();
                boolean paymentSuccessful = processPayment(subscription.getVehicle().getVin(), amount);
                
                if (!paymentSuccessful) {
                    // Send reminder notification
                    notificationService.sendPaymentReminderNotification(
                        subscription.getVehicle().getOwner(), amount);
                }
            }
        }
    }
    
    @Scheduled(cron = "0 0 3 * * ?") // Run daily at 3 AM
    public void processExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);
        
        for (Subscription subscription : expiredSubscriptions) {
            if (subscription.isInGracePeriod()) {
                // Still in grace period, send warning
                notificationService.sendGracePeriodWarningNotification(subscription.getVehicle().getOwner());
            } else {
                // Grace period ended, suspend subscription
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);
                
                Vehicle vehicle = subscription.getVehicle();
                vehicle.setSubscriptionActive(false);
                vehicleRepository.save(vehicle);
                
                // Log audit trail
                auditService.logVehicleAction(vehicle.getVin(), "SUBSCRIPTION_EXPIRED", "SYSTEM", 
                    "Subscription expired due to non-payment");
                
                // Send notification
                notificationService.sendSubscriptionExpiredNotification(vehicle.getOwner());
            }
        }
    }
    
    private BigDecimal calculatePrice(SubscriptionTier tier, BillingCycle cycle) {
        BigDecimal basePrice;
        switch (tier) {
            case BASIC: basePrice = BigDecimal.valueOf(9.99); break;
            case PREMIUM: basePrice = BigDecimal.valueOf(19.99); break;
            case ENTERPRISE: basePrice = BigDecimal.valueOf(49.99); break;
            default: basePrice = BigDecimal.valueOf(9.99);
        }
        
        if (cycle == BillingCycle.YEARLY) {
            return basePrice.multiply(BigDecimal.valueOf(10)); // 2 months free for yearly
        }
        return basePrice;
    }
    
    private boolean simulatePaymentProcessing(BigDecimal amount) {
        // Simulate payment processing with 95% success rate
        return Math.random() < 0.95;
    }
}
