package com.datvexemphim.api.dto.admin;

import java.time.LocalDateTime;

public record MovieGenreDTO(
        Long id,
        String name,
        String description,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
