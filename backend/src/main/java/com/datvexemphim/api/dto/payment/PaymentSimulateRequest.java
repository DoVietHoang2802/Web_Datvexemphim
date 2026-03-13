package com.datvexemphim.api.dto.payment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PaymentSimulateRequest(
        @NotEmpty List<Long> ticketIds,
        @NotNull Boolean success
) {
}

