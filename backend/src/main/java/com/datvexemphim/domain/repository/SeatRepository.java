package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomIdOrderByRowLabelAscColNumberAsc(Long roomId);
}

