package com.datvexemphim.api.dto.publicapi;

public record MovieDetailDto(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String posterUrl,
        String trailerUrl,
        String rating
) {
}

