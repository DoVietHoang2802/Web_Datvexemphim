package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.publicapi.SeatMapResponse;
import com.datvexemphim.service.SeatMapService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seatmap")
@CrossOrigin
public class SeatMapController {
    private final SeatMapService seatMapService;

    public SeatMapController(SeatMapService seatMapService) {
        this.seatMapService = seatMapService;
    }

    @GetMapping("/{showtimeId}")
    public SeatMapResponse seatMap(@PathVariable Long showtimeId) {
        return seatMapService.getSeatMap(showtimeId);
    }
}

