package com.datvexemphim.api.dto.ticket;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
        String bookingCode,
        String ticketCode,
        // Food order info
        Long foodOrderId,
        BigDecimal foodTotalPrice,
        String foodOrderStatus,
        List<FoodOrderItemSummary> foodItems
) {
    // Inner record for food item summary
    public record FoodOrderItemSummary(
            String name,
            Integer quantity,
            BigDecimal priceAtOrder,
            BigDecimal subtotal
    ) {}
}

