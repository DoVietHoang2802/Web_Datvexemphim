package com.datvexemphim.api.dto.admin;

import java.time.Instant;

public record AdminShowtimeDto(
        Long id,
        Long movieId,
        String movieTitle,
        Long roomId,
        String roomName,
        Instant startTime,
        Instant endTime,
        Long price
) {
}

