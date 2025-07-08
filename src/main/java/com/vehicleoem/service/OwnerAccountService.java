package com.vehicleoem.service;

import com.vehicleoem.model.OwnerAccount;
import com.vehicleoem.repository.OwnerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerAccountService {
    
    @Autowired
    private OwnerAccountRepository ownerAccountRepository;
    
    @Transactional
    public OwnerAccount createOwnerAccount(String accountId, String email, String firstName, String lastName) {
        // Check if account already exists
        if (ownerAccountRepository.existsByAccountId(accountId)) {
            throw new IllegalArgumentException("Account already exists: " + accountId);
        }
        
        if (ownerAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        
        OwnerAccount account = new OwnerAccount(accountId, email, firstName, lastName);
        return ownerAccountRepository.save(account);
    }
    
    public OwnerAccount findByAccountId(String accountId) {
        return ownerAccountRepository.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
    }
    
    public OwnerAccount findByEmail(String email) {
        return ownerAccountRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Account not found for email: " + email));
    }

    @Transactional
    public void verifyEmail(String accountId) {
        OwnerAccount account = findByAccountId(accountId);
        account.setEmailVerified(true);
        ownerAccountRepository.save(account);
    }
}
