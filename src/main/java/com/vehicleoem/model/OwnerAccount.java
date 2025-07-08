package com.vehicleoem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "owner_accounts")
public class OwnerAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true, length = 50)
    @Pattern(regexp = "^[A-Z0-9]{6,20}$", message = "Account ID must be 6-20 characters, alphanumeric uppercase")
    private String accountId;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(unique = true, length = 100)
    private String email;

    @NotBlank
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 20)
    private String state;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 50)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;
    
    // Constructors
    public OwnerAccount() {}
    
    public OwnerAccount(String accountId, String email, String firstName, String lastName) {
        this.accountId = accountId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Boolean getPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(Boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }

    // Business methods
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    public boolean isAccountActive() {
        return accountStatus == AccountStatus.ACTIVE && !isAccountLocked();
    }

    public void lockAccount(int lockDurationMinutes) {
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
        this.accountStatus = AccountStatus.LOCKED;
    }

    public void unlockAccount() {
        this.accountLockedUntil = null;
        this.failedLoginAttempts = 0;
        if (this.accountStatus == AccountStatus.LOCKED) {
            this.accountStatus = AccountStatus.ACTIVE;
        }
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            lockAccount(30); // Lock for 30 minutes after 5 failed attempts
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
