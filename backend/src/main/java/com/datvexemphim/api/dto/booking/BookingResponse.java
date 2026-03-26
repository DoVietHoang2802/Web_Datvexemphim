package com.datvexemphim.api.dto.booking;

import java.util.List;

public record BookingResponse(
        List<Long> ticketIds,
        Long totalAmount,
        String promoMessage
) {
}

