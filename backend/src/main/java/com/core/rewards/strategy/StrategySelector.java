package com.core.rewards.strategy;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StrategySelector {

    private final List<CalculationStrategy> strategies;

    public StrategySelector(List<CalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    public CalculationStrategy findStrategy(String customerTier) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(customerTier))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy configuration mapped for tier: " + customerTier));
    }
}
