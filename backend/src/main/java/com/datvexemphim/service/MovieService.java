package com.datvexemphim.service;

import com.datvexemphim.api.dto.publicapi.MovieDetailDto;
import com.datvexemphim.api.dto.publicapi.MovieSummaryDto;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.entity.MovieGenre;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.repository.MovieGenreRepository;
import com.datvexemphim.domain.repository.MovieRepository;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MovieGenreRepository movieGenreRepository;

    public MovieService(MovieRepository movieRepository, ShowtimeRepository showtimeRepository,
                       MovieGenreRepository movieGenreRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.movieGenreRepository = movieGenreRepository;
    }

    private String resolveGenreName(Long genreId) {
        if (genreId == null) return null;
        return movieGenreRepository.findById(genreId)
                .map(MovieGenre::getName)
                .orElse(null);
    }

    public List<MovieSummaryDto> listActive() {
        return movieRepository.findByActiveTrueOrderByIdDesc().stream()
                .map(m -> new MovieSummaryDto(m.getId(), m.getTitle(), m.getDurationMinutes(),
                        m.getPosterUrl(), m.getRating(),
                        resolveGenreName(m.getGenreId()), m.getGenreId()))
                .toList();
    }

    /**
     * Lấy danh sách phim đã chiếu (có showtime đã kết thúc)
     */
    public List<MovieSummaryDto> listPastMovies() {
        List<Showtime> pastShowtimes = showtimeRepository.findByStartTimeBefore(Instant.now());
        Set<Long> pastMovieIds = pastShowtimes.stream()
                .map(s -> s.getMovie().getId())
                .collect(Collectors.toSet());

        return movieRepository.findAllById(pastMovieIds).stream()
                .map(m -> new MovieSummaryDto(m.getId(), m.getTitle(), m.getDurationMinutes(),
                        m.getPosterUrl(), m.getRating(),
                        resolveGenreName(m.getGenreId()), m.getGenreId()))
                .toList();
    }

    public MovieDetailDto getDetail(Long id) {
        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        if (!m.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        return new MovieDetailDto(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(),
                m.getPosterUrl(), m.getTrailerUrl(), m.getRating(),
                resolveGenreName(m.getGenreId()), m.getGenreId());
    }
}

