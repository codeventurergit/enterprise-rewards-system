package com.core.rewards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * Immutable command record encapsulating the web contract request bounds.
 */
public record CalculationRequestDto(
    @NotBlank(message = "Customer account identifier is required.")
    String customerId,

    @NotNull(message = "Historical boundary start date is required.")
    @PastOrPresent(message = "Historical start date must reside in the past or present.")
    LocalDate calculationStartDate
) {}
