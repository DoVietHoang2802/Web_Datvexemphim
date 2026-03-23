package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatUpsertRequest(
        @NotNull Long roomId,
        @NotBlank String rowLabel,
        @NotNull Integer colNumber,
        @NotBlank String seatCode
) {
}

