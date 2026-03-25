package com.datvexemphim.api.dto.market;

import java.math.BigDecimal;
import java.time.Instant;

public record TicketMarketDTO(
        Long ticketId,
        String movieTitle,
        Instant showtimeStart,
        String roomName,
        String seatCode,
        BigDecimal price,
        String sellerFullName,
        Long sellerId,
        String ticketStatus,
        Instant listedAt
) {}
