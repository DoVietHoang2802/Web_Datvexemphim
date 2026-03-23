package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(Long movieId, Instant after);
    List<Showtime> findByStartTimeAfterOrderByStartTimeAsc(Instant after);
    List<Showtime> findByStartTimeBefore(Instant before);
    List<Showtime> findByRoomId(Long roomId);
}

