package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovieUpsertRequest(
        @NotBlank String title,
        String description,
        @NotNull Integer durationMinutes,
        String posterUrl,
        String trailerUrl,
        String rating,
        @NotNull Boolean active
) {
}

