package com.vehicleoem.service;

import com.vehicleoem.model.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class NotificationService {
    
    @Async
    public void sendKeyCreatedNotification(OwnerAccount owner, DigitalKey digitalKey) {
        String subject = "New Digital Key Created";
        String message = String.format(
            "Hello %s,\n\nA new digital key has been created for your vehicle %s.\n\n" +
            "Key ID: %s\nKey Type: %s\nDevice: %s\n\n" +
            "If you did not authorize this key creation, please contact support immediately.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            digitalKey.getVehicle().getVin(),
            digitalKey.getKeyId(),
            digitalKey.getKeyType(),
            digitalKey.getDeviceOem()
        );
        
        sendEmail(owner.getEmail(), subject, message);
        sendSMS(owner.getPhoneNumber(), "New digital key created for your vehicle. Check your email for details.");
    }
    
    @Async
    public void sendFriendKeyInvitation(String friendEmail, String friendName, OwnerAccount owner, DigitalKey digitalKey) {
        String subject = "You've been invited to access a vehicle";
        String message = String.format(
            "Hello %s,\n\n%s %s has shared access to their vehicle with you.\n\n" +
            "Vehicle: %s %s %s\nAccess Level: %s\n" +
            "Valid Until: %s\n\n" +
            "To accept this invitation and set up your digital key, please download our mobile app " +
            "and use invitation code: %s\n\n" +
            "Best regards,\nVehicle OEM Team",
            friendName != null ? friendName : "Friend",
            owner.getFirstName(),
            owner.getLastName(),
            digitalKey.getVehicle().getMake(),
            digitalKey.getVehicle().getModel(),
            digitalKey.getVehicle().getYear(),
            digitalKey.getPermissionLevel().getDescription(),
            digitalKey.getExpiresAt(),
            digitalKey.getKeyId()
        );
        
        sendEmail(friendEmail, subject, message);
    }
    
    @Async
    public void sendKeyStatusChangeNotification(OwnerAccount owner, DigitalKey digitalKey, String action) {
        String subject = "Digital Key Status Changed";
        String message = String.format(
            "Hello %s,\n\nThe status of digital key %s has been changed.\n\n" +
            "Action: %s\nNew Status: %s\nVehicle: %s\n\n" +
            "If you did not authorize this change, please contact support immediately.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            digitalKey.getKeyId(),
            action,
            digitalKey.getStatus(),
            digitalKey.getVehicle().getVin()
        );
        
        sendEmail(owner.getEmail(), subject, message);
    }
    
    @Async
    public void sendSubscriptionUpgradeNotification(OwnerAccount owner, SubscriptionTier oldTier, SubscriptionTier newTier) {
        String subject = "Subscription Upgraded Successfully";
        String message = String.format(
            "Hello %s,\n\nYour subscription has been successfully upgraded!\n\n" +
            "Previous Plan: %s\nNew Plan: %s\n\n" +
            "You now have access to:\n" +
            "- Up to %d digital keys\n" +
            "- %s\n" +
            "- %s\n\n" +
            "Thank you for choosing our premium services!\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            oldTier.name(),
            newTier.name(),
            newTier.getMaxKeys(),
            newTier.isFriendKeysAllowed() ? "Friend key sharing" : "Owner keys only",
            newTier.isAnalyticsEnabled() ? "Advanced analytics" : "Basic features"
        );
        
        sendEmail(owner.getEmail(), subject, message);
    }
    
    @Async
    public void sendSubscriptionCancellationNotification(OwnerAccount owner, String reason) {
        String subject = "Subscription Cancelled";
        String message = String.format(
            "Hello %s,\n\nYour subscription has been cancelled.\n\n" +
            "Reason: %s\n\n" +
            "Your digital keys will remain active until the end of your current billing period. " +
            "After that, you'll need to reactivate your subscription to continue using digital key services.\n\n" +
            "We're sorry to see you go. If you have any feedback, please let us know.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            reason
        );
        
        sendEmail(owner.getEmail(), subject, message);
    }
    
    @Async
    public void sendPaymentFailureNotification(OwnerAccount owner, BigDecimal amount) {
        String subject = "Payment Failed - Action Required";
        String message = String.format(
            "Hello %s,\n\nWe were unable to process your payment of $%.2f for your vehicle subscription.\n\n" +
            "Please update your payment method or contact your bank to resolve this issue. " +
            "Your subscription will enter a grace period, and services may be suspended if payment is not received within 7 days.\n\n" +
            "To update your payment method, please log into your account or contact support.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            amount
        );
        
        sendEmail(owner.getEmail(), subject, message);
        sendSMS(owner.getPhoneNumber(), String.format("Payment of $%.2f failed. Please update your payment method to avoid service interruption.", amount));
    }
    
    @Async
    public void sendPaymentReminderNotification(OwnerAccount owner, BigDecimal amount) {
        String subject = "Payment Reminder";
        String message = String.format(
            "Hello %s,\n\nThis is a friendly reminder that your subscription payment of $%.2f is due soon.\n\n" +
            "Your payment will be automatically processed from your saved payment method. " +
            "Please ensure your payment information is up to date.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName(),
            amount
        );
        
        sendEmail(owner.getEmail(), subject, message);
    }
    
    @Async
    public void sendGracePeriodWarningNotification(OwnerAccount owner) {
        String subject = "Urgent: Subscription Grace Period Ending";
        String message = String.format(
            "Hello %s,\n\nYour subscription is in a grace period due to payment issues. " +
            "Your services will be suspended soon if payment is not received.\n\n" +
            "Please update your payment method immediately to avoid service interruption.\n\n" +
            "Contact support if you need assistance.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName()
        );
        
        sendEmail(owner.getEmail(), subject, message);
        sendSMS(owner.getPhoneNumber(), "URGENT: Your vehicle subscription grace period is ending. Update payment method now.");
    }
    
    @Async
    public void sendSubscriptionExpiredNotification(OwnerAccount owner) {
        String subject = "Subscription Expired";
        String message = String.format(
            "Hello %s,\n\nYour vehicle subscription has expired due to non-payment. " +
            "All digital key services have been suspended.\n\n" +
            "To reactivate your subscription and restore access to your digital keys, " +
            "please log into your account and update your payment method.\n\n" +
            "Contact support if you need assistance.\n\n" +
            "Best regards,\nVehicle OEM Team",
            owner.getFirstName()
        );
        
        sendEmail(owner.getEmail(), subject, message);
    }
    
    @Async
    public void sendSecurityAlertNotification(OwnerAccount owner, String alertType, String details) {
        String subject = "Security Alert - " + alertType;
        String message = String.format(
            "Hello %s,\n\nWe detected unusual activity on your account:\n\n" +
            "Alert Type: %s\nDetails: %s\nTime: %s\n\n" +
            "If this was not authorized by you, please contact support immediately and consider changing your account password.\n\n" +
            "Best regards,\nVehicle OEM Security Team",
            owner.getFirstName(),
            alertType,
            details,
            java.time.LocalDateTime.now()
        );
        
        sendEmail(owner.getEmail(), subject, message);
        sendSMS(owner.getPhoneNumber(), "Security alert: " + alertType + ". Check your email for details.");
    }
    
    private void sendEmail(String email, String subject, String message) {
        // In a real implementation, this would integrate with an email service like SendGrid, AWS SES, etc.
        System.out.println("EMAIL TO: " + email);
        System.out.println("SUBJECT: " + subject);
        System.out.println("MESSAGE: " + message);
        System.out.println("---");
    }
    
    @Async
    public void sendKeyRevokedNotification(String friendEmail, String friendName, Vehicle vehicle, String reason) {
        String subject = "Vehicle Access Revoked";
        String message = String.format(
            "Hello %s,\n\nYour access to the vehicle %s %s %s has been revoked.\n\n" +
            "Reason: %s\n\n" +
            "If you have any questions, please contact the vehicle owner.\n\n" +
            "Best regards,\nVehicle OEM Team",
            friendName != null ? friendName : "Friend",
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getYear(),
            reason
        );

        sendEmail(friendEmail, subject, message);
    }

    @Async
    public void sendPermissionUpdateNotification(String friendEmail, String friendName, Vehicle vehicle,
                                               PermissionLevel oldLevel, PermissionLevel newLevel) {
        String subject = "Vehicle Access Permissions Updated";
        String message = String.format(
            "Hello %s,\n\nYour access permissions for the vehicle %s %s %s have been updated.\n\n" +
            "Previous Access: %s\nNew Access: %s\n\n" +
            "Best regards,\nVehicle OEM Team",
            friendName != null ? friendName : "Friend",
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getYear(),
            oldLevel.getDescription(),
            newLevel.getDescription()
        );

        sendEmail(friendEmail, subject, message);
    }

    @Async
    public void sendKeyRestrictionsUpdateNotification(String friendEmail, String friendName, Vehicle vehicle) {
        String subject = "Vehicle Access Restrictions Updated";
        String message = String.format(
            "Hello %s,\n\nThe usage restrictions for your access to the vehicle %s %s %s have been updated.\n\n" +
            "Please check the mobile app for the latest restrictions.\n\n" +
            "Best regards,\nVehicle OEM Team",
            friendName != null ? friendName : "Friend",
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getYear()
        );

        sendEmail(friendEmail, subject, message);
    }

    private void sendSMS(String phoneNumber, String message) {
        // In a real implementation, this would integrate with an SMS service like Twilio, AWS SNS, etc.
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            System.out.println("SMS TO: " + phoneNumber);
            System.out.println("MESSAGE: " + message);
            System.out.println("---");
        }
    }
}
