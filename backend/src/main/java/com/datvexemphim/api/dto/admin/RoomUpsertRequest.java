package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomUpsertRequest(
        @NotBlank String name,
        @NotNull Integer totalRows,
        @NotNull Integer totalCols
) {
}

