package com.datvexemphim.service.admin;

import com.datvexemphim.api.dto.admin.MovieGenreDTO;
import com.datvexemphim.api.dto.admin.MovieGenreUpsertRequest;
import com.datvexemphim.domain.entity.MovieGenre;
import com.datvexemphim.domain.repository.MovieGenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieGenreService {
    private final MovieGenreRepository movieGenreRepository;

    public MovieGenreService(MovieGenreRepository movieGenreRepository) {
        this.movieGenreRepository = movieGenreRepository;
    }

    public List<MovieGenreDTO> list() {
        return movieGenreRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<MovieGenreDTO> listActive() {
        return movieGenreRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public MovieGenreDTO get(Long id) {
        MovieGenre genre = movieGenreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
        return toDto(genre);
    }

    @Transactional
    public MovieGenreDTO create(MovieGenreUpsertRequest req) {
        MovieGenre genre = new MovieGenre();
        genre.setName(req.name());
        genre.setDescription(req.description());
        genre.setIsActive(req.isActive() != null ? req.isActive() : true);
        genre = movieGenreRepository.save(genre);
        return toDto(genre);
    }

    @Transactional
    public MovieGenreDTO update(Long id, MovieGenreUpsertRequest req) {
        MovieGenre genre = movieGenreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
        genre.setName(req.name());
        genre.setDescription(req.description());
        if (req.isActive() != null) {
            genre.setIsActive(req.isActive());
        }
        genre = movieGenreRepository.save(genre);
        return toDto(genre);
    }

    @Transactional
    public void delete(Long id) {
        if (!movieGenreRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        movieGenreRepository.deleteById(id);
    }

    private MovieGenreDTO toDto(MovieGenre genre) {
        return new MovieGenreDTO(
                genre.getId(),
                genre.getName(),
                genre.getDescription(),
                genre.getIsActive(),
                genre.getCreatedAt(),
                genre.getUpdatedAt()
        );
    }
}
