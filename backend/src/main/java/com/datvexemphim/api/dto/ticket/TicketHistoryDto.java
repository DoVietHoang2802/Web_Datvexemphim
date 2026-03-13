package com.datvexemphim.api.dto.ticket;

import java.time.Instant;

public record TicketHistoryDto(
        Long ticketId,
        String status,
        Instant bookedAt,
        Instant cancelledAt,
        Long showtimeId,
        Instant startTime,
        String movieTitle,
        String roomName,
        String seatCode,
        Long price,
        String bookingCode
) {
}

