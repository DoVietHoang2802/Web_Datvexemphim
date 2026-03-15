package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.AdminMovieDto;
import com.datvexemphim.api.dto.admin.MovieUpsertRequest;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.entity.MovieGenre;
import com.datvexemphim.domain.repository.MovieGenreRepository;
import com.datvexemphim.domain.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminMovieService {
    private final MovieRepository movieRepository;
    private final MovieGenreRepository movieGenreRepository;

    public AdminMovieService(MovieRepository movieRepository, MovieGenreRepository movieGenreRepository) {
        this.movieRepository = movieRepository;
        this.movieGenreRepository = movieGenreRepository;
    }

    public List<AdminMovieDto> list() {
        return movieRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Movie get(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    public Movie create(MovieUpsertRequest req) {
        Movie m = new Movie();
        apply(m, req);
        return movieRepository.save(m);
    }

    public Movie update(Long id, MovieUpsertRequest req) {
        Movie m = get(id);
        apply(m, req);
        return movieRepository.save(m);
    }

    public void delete(Long id) {
        movieRepository.deleteById(id);
    }

    private void apply(Movie m, MovieUpsertRequest req) {
        m.setTitle(req.title());
        m.setDescription(req.description());
        m.setDurationMinutes(req.durationMinutes());
        m.setPosterUrl(req.posterUrl());
        m.setTrailerUrl(req.trailerUrl());
        m.setRating(req.rating());
        m.setGenreId(req.genreId());
        m.setActive(Boolean.TRUE.equals(req.active()));
    }

    private AdminMovieDto toDto(Movie m) {
        String genreName = null;
        if (m.getGenreId() != null) {
            genreName = movieGenreRepository.findById(m.getGenreId())
                    .map(MovieGenre::getName)
                    .orElse(null);
        }
        return new AdminMovieDto(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getDurationMinutes(),
                m.getPosterUrl(),
                m.getTrailerUrl(),
                m.getRating(),
                m.getGenreId(),
                genreName,
                m.isActive()
        );
    }
}

