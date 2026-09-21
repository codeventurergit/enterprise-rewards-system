package com.core.rewards.strategy;

import com.core.rewards.repository.TransactionProjection;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StandardBracketedStrategy implements CalculationStrategy {

    @Override
    public double calculateCumulativePoints(List<TransactionProjection> history) {
        double totalPoints = 0.0;

        for (TransactionProjection tx : history) {
            double spend = tx.getAmount();

            // Threshold guard: No points awarded for purchases at or below $50
            if (spend <= 50.0) {
                continue;
            }

            // Bracket 1: Purchases up to and including $100 earn 1 point per dollar over $50
            if (spend <= 100.0) {
                totalPoints += (spend - 50.0) * 1;
            } 
            
            // Bracket 2: Purchases over $100 earn 1 point per dollar for the $50-$100 range (50 points),
            // plus 2 points for every dollar exceeding $100.
            // Example Validation: $120 purchase = (100-50)*1 + (120-100)*2 = 50 + 40 = 90 points.
            else {
                double baseTierPoints = (100.0 - 50.0) * 1; // Exactly 50 points earned for the $50-$100 range
                double bonusTierPoints = (spend - 100.0) * 2; // Double points strictly for the amount OVER $100
                totalPoints += baseTierPoints + bonusTierPoints;
            }
        }

        return totalPoints;
    }

    @Override
    public boolean isApplicable(String customerTier) {
        return "STANDARD".equalsIgnoreCase(customerTier);
    }
}
