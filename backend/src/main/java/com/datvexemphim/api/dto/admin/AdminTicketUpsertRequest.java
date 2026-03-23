package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotNull;

public record AdminTicketUpsertRequest(
        @NotNull Long showtimeId,
        @NotNull Long seatId,
        @NotNull Long ownerUserId,
        @NotNull String status
) {
}

