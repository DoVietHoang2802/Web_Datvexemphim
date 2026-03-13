package com.datvexemphim.api.dto.publicapi;

public record MovieSummaryDto(
        Long id,
        String title,
        Integer durationMinutes,
        String posterUrl,
        String rating
) {
}

