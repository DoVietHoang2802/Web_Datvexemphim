package com.datvexemphim.api.dto.admin;

import java.time.Instant;

public record AdminTicketDto(
        Long ticketId,
        String status,
        Instant bookedAt,
        Long showtimeId,
        Instant startTime,
        String movieTitle,
        String roomName,
        String seatCode,
        String ownerEmail,
        Long price,
        String bookingCode
) {
}

