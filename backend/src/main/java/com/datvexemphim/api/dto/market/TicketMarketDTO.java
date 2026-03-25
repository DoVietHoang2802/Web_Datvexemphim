package com.datvexemphim.api.dto.market;

import java.time.Instant;

public record TicketMarketDTO(
        Long ticketId,
        String movieTitle,
        Instant showtimeStart,
        String roomName,
        String seatCode,
        Long price,
        String sellerFullName,
        Long sellerId,
        String ticketStatus,
        Instant listedAt
) {}
