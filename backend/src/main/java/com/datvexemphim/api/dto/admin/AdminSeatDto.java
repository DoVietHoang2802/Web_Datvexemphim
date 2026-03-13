package com.datvexemphim.api.dto.admin;

public record AdminSeatDto(
        Long id,
        Long roomId,
        String seatCode,
        String rowLabel,
        Integer colNumber
) {
}

