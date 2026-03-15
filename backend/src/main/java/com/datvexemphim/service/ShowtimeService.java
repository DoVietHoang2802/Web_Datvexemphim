package com.datvexemphim.service;

import com.datvexemphim.api.dto.publicapi.ShowtimeDto;
import com.datvexemphim.domain.entity.Showtime;
import com.datvexemphim.domain.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional
    public List<ShowtimeDto> listUpcomingByMovie(Long movieId) {
        return showtimeRepository.findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(movieId, Instant.now())
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public List<ShowtimeDto> listUpcomingAll() {
        // Hiển thị tất cả suất chiếu trong ngày hôm nay và tương lai
        return showtimeRepository.findByStartTimeAfterOrderByStartTimeAsc(Instant.now().minusSeconds(3600))
                .stream().map(this::toDto).toList();
    }

    private ShowtimeDto toDto(Showtime s) {
        return new ShowtimeDto(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getRoom().getId(),
                s.getRoom().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice(),
                s.getMovie().getGenre(),
                s.getMovie().getPosterUrl()
        );
    }
}

