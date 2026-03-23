package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ShowtimeUpsertRequest(
        @NotNull Long movieId,
        @NotNull Long roomId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        @NotNull Long price
) {
}

