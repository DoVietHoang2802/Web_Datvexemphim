package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.publicapi.MovieDetailDto;
import com.datvexemphim.api.dto.publicapi.MovieSummaryDto;
import com.datvexemphim.api.dto.admin.MovieGenreDTO;
import com.datvexemphim.service.MovieService;
import com.datvexemphim.service.admin.MovieGenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin
public class MovieController {
    private final MovieService movieService;
    private final MovieGenreService movieGenreService;

    public MovieController(MovieService movieService, MovieGenreService movieGenreService) {
        this.movieService = movieService;
        this.movieGenreService = movieGenreService;
    }

    @GetMapping
    public List<MovieSummaryDto> list() {
        return movieService.listActive();
    }

    @GetMapping("/past")
    public List<MovieSummaryDto> listPast() {
        return movieService.listPastMovies();
    }

    @GetMapping("/{id}")
    public MovieDetailDto detail(@PathVariable Long id) {
        return movieService.getDetail(id);
    }

    // Public endpoint to get genres (no auth required)
    @GetMapping("/genres")
    public List<MovieGenreDTO> listGenres() {
        return movieGenreService.listActive();
    }
}

