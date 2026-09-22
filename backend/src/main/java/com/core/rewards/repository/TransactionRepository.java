package com.core.rewards.repository;

import com.core.rewards.model.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<UserTransaction, Long> {

    @Query(nativeQuery = true, value = 
        "SELECT amount, created_at as createdAt FROM points_ledger " +
        "WHERE customer_id = :customerId " +
        "AND created_at >= :startDate")
    List<TransactionProjection> fetchReadOnlyHistory(
        @Param("customerId") String customerId, 
        @Param("startDate") LocalDate startDate
    );
}
