package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.MovieUpsertRequest;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.service.admin.AdminMovieService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
@CrossOrigin
public class AdminMovieController {
    private final AdminMovieService adminMovieService;

    public AdminMovieController(AdminMovieService adminMovieService) {
        this.adminMovieService = adminMovieService;
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
}

