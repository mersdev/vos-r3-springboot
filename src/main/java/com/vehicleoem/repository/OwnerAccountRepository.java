package com.vehicleoem.repository;

import com.vehicleoem.model.OwnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OwnerAccountRepository extends JpaRepository<OwnerAccount, Long> {
    Optional<OwnerAccount> findByAccountId(String accountId);
    Optional<OwnerAccount> findByEmail(String email);
    boolean existsByAccountId(String accountId);
    boolean existsByEmail(String email);
}
