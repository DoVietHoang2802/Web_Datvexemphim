package com.datvexemphim.api.dto.admin;

public record AdminMovieDto(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String posterUrl,
        String trailerUrl,
        String rating,
        Long genreId,
        String genreName,
        Boolean active
) {
}
