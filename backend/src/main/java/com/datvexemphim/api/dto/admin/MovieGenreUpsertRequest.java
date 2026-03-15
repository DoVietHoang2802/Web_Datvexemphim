package com.datvexemphim.api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record MovieGenreUpsertRequest(
        @NotBlank String name,
        String description,
        Boolean isActive
) {
}
