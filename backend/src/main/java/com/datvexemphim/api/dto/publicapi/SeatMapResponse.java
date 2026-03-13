package com.datvexemphim.api.dto.publicapi;

import java.util.List;

public record SeatMapResponse(
        Long showtimeId,
        Long roomId,
        String roomName,
        int totalRows,
        int totalCols,
        List<SeatMapSeatDto> seats
) {
}

