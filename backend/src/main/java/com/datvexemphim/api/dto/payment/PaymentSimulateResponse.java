package com.datvexemphim.api.dto.payment;

public record PaymentSimulateResponse(
        Long paymentId,
        String bookingCode,
        String status,
        Long amount
) {
}

