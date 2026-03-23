package com.datvexemphim.api.controller;

import com.datvexemphim.api.dto.publicapi.CreateReviewRequest;
import com.datvexemphim.api.dto.publicapi.MovieReviewDto;
import com.datvexemphim.service.MovieReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MovieReviewController {
    private final MovieReviewService reviewService;

    public MovieReviewController(MovieReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<MovieReviewDto>> getReviews(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getByMovie(movieId));
    }

    @GetMapping("/movies/{movieId}/reviews/stats")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getMovieRatingStats(movieId));
    }

    @PostMapping("/movies/reviews")
    public ResponseEntity<MovieReviewDto> createReview(@RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.createOrUpdate(request));
    }

    @DeleteMapping("/movies/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }
}
