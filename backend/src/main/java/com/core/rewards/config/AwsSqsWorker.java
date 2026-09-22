package com.core.rewards.config;

import com.core.rewards.dto.CalculationRequestDto;
import com.core.rewards.service.EnterpriseCalculationEngine;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("aws") 
public class AwsSqsWorker {

    private final EnterpriseCalculationEngine calculationEngine;

    public AwsSqsWorker(EnterpriseCalculationEngine calculationEngine) {
        this.calculationEngine = calculationEngine;
    }

    @SqsListener("cable-rewards-calculation-queue")
    public void consumeMessage(CalculationRequestDto command) {
        
        String traceId = "SQS-JOB";
        calculationEngine.updateAndRecalculatePoints(command, traceId);
    }
}
