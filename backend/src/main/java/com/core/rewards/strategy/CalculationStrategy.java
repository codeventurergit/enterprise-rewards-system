package com.core.rewards.strategy;

import com.core.rewards.repository.TransactionProjection;
import java.util.List;

public interface CalculationStrategy {
    double calculateCumulativePoints(List<TransactionProjection> history);
    boolean isApplicable(String customerTier);
}
