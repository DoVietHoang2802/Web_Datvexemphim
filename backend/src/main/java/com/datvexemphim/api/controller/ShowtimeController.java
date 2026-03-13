package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.publicapi.ShowtimeDto;
import com.datvexemphim.service.ShowtimeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin
public class ShowtimeController {
    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public List<ShowtimeDto> listUpcomingAll() {
        return showtimeService.listUpcomingAll();
    }

    @GetMapping("/movie/{movieId}")
    public List<ShowtimeDto> listUpcomingByMovie(@PathVariable Long movieId) {
        return showtimeService.listUpcomingByMovie(movieId);
    }
}

