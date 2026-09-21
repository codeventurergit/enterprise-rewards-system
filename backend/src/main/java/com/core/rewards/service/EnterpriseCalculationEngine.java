package com.core.rewards.service;

import com.core.rewards.dto.CalculationRequestDto;
import com.core.rewards.model.UserAccount;
import com.core.rewards.repository.AccountRepository;
import com.core.rewards.repository.TransactionRepository;
import com.core.rewards.repository.TransactionProjection;
import com.core.rewards.strategy.CalculationStrategy;
import com.core.rewards.strategy.StrategySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EnterpriseCalculationEngine {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseCalculationEngine.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final StrategySelector strategySelector;

    public EnterpriseCalculationEngine(TransactionRepository transactionRepository,
                                       AccountRepository accountRepository,
                                       StrategySelector strategySelector) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.strategySelector = strategySelector;
    }

    /**
     * Coordinates the read-compute-write loop for historical calculation out-of-band.
     * Retries automatically if an optimistic locking collision occurs under high concurrent traffic.
     */
    @Retryable(
        retryFor = { ObjectOptimisticLockingFailureException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0)
    )
    @Transactional(
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = Exception.class
    )
    public double updateAndRecalculatePoints(CalculationRequestDto command, String traceId) {
        log.info("[TraceId: {}] Streaming multi-month database transaction blocks for customer: {}", 
                 traceId, command.customerId());

        // 1. READ: Stream columns directly using non-managed Native Projections over the index schema
        List<TransactionProjection> historyData = transactionRepository
                .fetchReadOnlyHistory(command.customerId(), command.calculationStartDate());

        if (historyData.isEmpty()) {
            log.warn("[TraceId: {}] No transaction rows found for the specified date boundaries.", traceId);
            return 0.0;
        }

        // 2. COMPUTE: Delegate multi-bracket processing to the selected Strategy class in a single call
        CalculationStrategy strategy = strategySelector.findStrategy("STANDARD");
        double freshlyCalculatedTotal = strategy.calculateCumulativePoints(historyData);

        // 3. WRITE: Fetch state target and save (Hibernate verifies the @Version property column here)
        UserAccount account = accountRepository.findById(Long.parseLong(command.customerId()))
                .orElseThrow(() -> new RuntimeException("Target account tracker missing from datastore context."));

        account.setTotalRewardsBalance(freshlyCalculatedTotal);
        accountRepository.save(account);

        log.info("[TraceId: {}] Calculation committed successfully. Synced balance: {}", traceId, freshlyCalculatedTotal);
        return freshlyCalculatedTotal;
    }
}
