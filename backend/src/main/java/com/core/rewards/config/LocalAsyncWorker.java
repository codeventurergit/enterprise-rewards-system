package com.core.rewards.config;

import com.core.rewards.dto.CalculationRequestDto;
import com.core.rewards.service.EnterpriseCalculationEngine;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Profile("local") // Active by default
public class LocalAsyncWorker {

    private final EnterpriseCalculationEngine calculationEngine;

    public LocalAsyncWorker(EnterpriseCalculationEngine calculationEngine) {
        this.calculationEngine = calculationEngine;
    }

    @Async
    public void processTask(CalculationRequestDto command, String traceId) {
        // Fallback execution mimics the background SQS queue entirely in app RAM
        calculationEngine.updateAndRecalculatePoints(command, traceId);
    }
}
