package com.cable.rewards.repository;

import com.core.rewards.model.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<UserTransaction, Long> {

    /**
     * Streams columns directly from the database into a non-managed Java DTO interface.
     * Leverages the idx_customer_date composite index to avoid slow full-table scans.
     */
    @Query(value = "SELECT amount, tx_type as txType, created_at as createdAt " +
                   "FROM points_ledger WHERE customer_id = :customerId AND created_at >= :startDate", 
           nativeQuery = true)
    List<TransactionProjection> fetchReadOnlyHistory(
            @Param("customerId") String customerId, 
            @Param("startDate") LocalDate startDate
    );
}
