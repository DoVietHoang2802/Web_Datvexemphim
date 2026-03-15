package com.datvexemphim.service;

import com.datvexemphim.api.dto.publicapi.MovieDetailDto;
import com.datvexemphim.api.dto.publicapi.MovieSummaryDto;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieSummaryDto> listActive() {
        return movieRepository.findByActiveTrueOrderByIdDesc().stream()
                .map(m -> new MovieSummaryDto(m.getId(), m.getTitle(), m.getDurationMinutes(), m.getPosterUrl(), m.getRating(), m.getGenre(), m.getGenreId()))
                .toList();
    }

    public MovieDetailDto getDetail(Long id) {
        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        if (!m.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        return new MovieDetailDto(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(),
                m.getPosterUrl(), m.getTrailerUrl(), m.getRating(), m.getGenre(), m.getGenreId());
    }
}

