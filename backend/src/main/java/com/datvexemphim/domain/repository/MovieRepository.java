package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActiveTrueOrderByIdDesc();
}

