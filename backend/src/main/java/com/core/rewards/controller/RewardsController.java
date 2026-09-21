package com.core.rewards.controller;

import com.core.rewards.dto.ActionTrackerResponse;
import com.core.rewards.dto.CalculationRequestDto;
import com.core.rewards.service.EnterpriseCalculationEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/v1/rewards")
@Tag(name = "Rewards Calculation Engine", description = "Asynchronous pipelines for multi-month transactional data processing.")
public class RewardsController {

    private final EnterpriseCalculationEngine calculationEngine;

    public RewardsController(EnterpriseCalculationEngine calculationEngine) {
        this.calculationEngine = calculationEngine;
    }

    @PostMapping("/calculations")
    @Operation(
        summary = "Submit Multi-Month Transaction Records for Evaluation",
        description = "Validates the command payload object, provisions an immutable Trace ID, and routes execution to decoupled background strategies."
    )
    @ApiResponse(responseCode = "202", description = "Ledger evaluation request successfully accepted and queued.")
    public ResponseEntity<ActionTrackerResponse> triggerRecalculation(
            @Valid @RequestBody CalculationRequestDto requestDto) {
        
        // Generate an audit trace string for microservice tracking visibility
        String traceId = "TRC-" + UUID.randomUUID().toString().substring(0, 8);

        /* does not wait for the database calculation to finish; instead, it dumps the task onto a background queue thread 
        and releases the web request immediately */

        calculationEngine.updateAndRecalculatePoints(requestDto, traceId);

        ActionTrackerResponse response = new ActionTrackerResponse(
                traceId,
                "ACCEPTED",
                "Historical calculation task successfully initiated."
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
