package com.datvexemphim.api.dto.publicapi;

public record SeatMapSeatDto(
        Long seatId,
        String seatCode,
        String rowLabel,
        Integer colNumber,
        boolean booked
) {
}

