package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.publicapi.MovieDetailDto;
import com.datvexemphim.api.dto.publicapi.MovieSummaryDto;
import com.datvexemphim.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieSummaryDto> list() {
        return movieService.listActive();
    }

    @GetMapping("/{id}")
    public MovieDetailDto detail(@PathVariable Long id) {
        return movieService.getDetail(id);
    }
}

