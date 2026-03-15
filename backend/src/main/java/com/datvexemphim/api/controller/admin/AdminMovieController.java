package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.MovieGenreDTO;
import com.datvexemphim.api.dto.admin.MovieGenreUpsertRequest;
import com.datvexemphim.api.dto.admin.MovieUpsertRequest;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.service.admin.AdminMovieService;
import com.datvexemphim.service.admin.MovieGenreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
@CrossOrigin
public class AdminMovieController {
    private final AdminMovieService adminMovieService;
    private final MovieGenreService movieGenreService;

    public AdminMovieController(AdminMovieService adminMovieService, MovieGenreService movieGenreService) {
        this.adminMovieService = adminMovieService;
        this.movieGenreService = movieGenreService;
    }

    @GetMapping
    public List<Movie> list() {
        return adminMovieService.list();
    }

    @GetMapping("/{id}")
    public Movie get(@PathVariable Long id) {
        return adminMovieService.get(id);
    }

    @PostMapping
    public Movie create(@Valid @RequestBody MovieUpsertRequest req) {
        return adminMovieService.create(req);
    }

    @PutMapping("/{id}")
    public Movie update(@PathVariable Long id, @Valid @RequestBody MovieUpsertRequest req) {
        return adminMovieService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminMovieService.delete(id);
    }

    // Genre endpoints
    @GetMapping("/genres")
    public List<MovieGenreDTO> listGenres() {
        return movieGenreService.list();
    }

    @GetMapping("/genres/active")
    public List<MovieGenreDTO> listActiveGenres() {
        return movieGenreService.listActive();
    }

    @GetMapping("/genres/{id}")
    public MovieGenreDTO getGenre(@PathVariable Long id) {
        return movieGenreService.get(id);
    }

    @PostMapping("/genres")
    public MovieGenreDTO createGenre(@Valid @RequestBody MovieGenreUpsertRequest req) {
        return movieGenreService.create(req);
    }

    @PutMapping("/genres/{id}")
    public MovieGenreDTO updateGenre(@PathVariable Long id, @Valid @RequestBody MovieGenreUpsertRequest req) {
        return movieGenreService.update(id, req);
    }

    @DeleteMapping("/genres/{id}")
    public void deleteGenre(@PathVariable Long id) {
        movieGenreService.delete(id);
    }
}

