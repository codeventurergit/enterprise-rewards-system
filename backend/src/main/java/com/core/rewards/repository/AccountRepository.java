package com.core.rewards.repository;

import com.core.rewards.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<UserAccount, Long> {
    
    // Method lookup to locate the specific user tracking record
    Optional<UserAccount> findById(Long id);
}
