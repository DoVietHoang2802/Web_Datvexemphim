package com.datvexemphim.api.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBookingRequest(
        @NotNull Long showtimeId,
        @NotEmpty List<Long> seatIds
) {
}

