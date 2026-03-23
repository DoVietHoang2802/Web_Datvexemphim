package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {
    List<MovieReview> findByMovieIdOrderByCreatedAtDesc(Long movieId);
    Optional<MovieReview> findByMovieIdAndUserId(Long movieId, Long userId);
    boolean existsByMovieIdAndUserId(Long movieId, Long userId);
}
