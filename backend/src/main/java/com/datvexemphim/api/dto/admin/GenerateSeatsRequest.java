package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotNull;

public record GenerateSeatsRequest(
        @NotNull Long roomId,
        @NotNull Integer totalRows,
        @NotNull Integer totalCols
) {
}

