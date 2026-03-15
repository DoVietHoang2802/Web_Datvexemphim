package com.datvexemphim.api.dto.publicapi;

import java.time.Instant;

public record ShowtimeDto(
        Long id,
        Long movieId,
        String movieTitle,
        Long roomId,
        String roomName,
        Instant startTime,
        Instant endTime,
        Long price,
        String genre,
        String posterUrl
) {
}

