package com.datvexemphim.service;

import com.datvexemphim.api.dto.publicapi.CreateReviewRequest;
import com.datvexemphim.api.dto.publicapi.MovieReviewDto;
import com.datvexemphim.domain.entity.Movie;
import com.datvexemphim.domain.entity.MovieReview;
import com.datvexemphim.domain.entity.User;
import com.datvexemphim.domain.repository.MovieRepository;
import com.datvexemphim.domain.repository.MovieReviewRepository;
import com.datvexemphim.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieReviewService {
    private final MovieReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public MovieReviewService(MovieReviewRepository reviewRepository, MovieRepository movieRepository,
                              UserRepository userRepository, CurrentUserService currentUserService) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<MovieReviewDto> getByMovie(Long movieId) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMovieRatingStats(Long movieId) {
        List<MovieReview> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);

        if (reviews.isEmpty()) {
            return Map.of(
                "averageRating", 0.0,
                "totalReviews", 0,
                "ratingDistribution", Map.of(1, 0, 2, 0, 3, 0, 4, 0, 5, 0)
            );
        }

        double average = reviews.stream().mapToInt(MovieReview::getRating).average().orElse(0);

        Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(MovieReview::getRating, Collectors.counting()));

        // Ensure all ratings 1-5 are present
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return Map.of(
            "averageRating", Math.round(average * 10) / 10.0,
            "totalReviews", reviews.size(),
            "ratingDistribution", distribution
        );
    }

    @Transactional
    public MovieReviewDto createOrUpdate(CreateReviewRequest request) {
        User user = currentUserService.requireUser();
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Phim không tồn tại"));

        // Check if user already reviewed
        MovieReview review = reviewRepository.findByMovieIdAndUserId(request.getMovieId(), user.getId())
                .orElse(null);

        if (review == null) {
            review = new MovieReview();
            review.setMovie(movie);
            review.setUser(user);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        return toDto(review);
    }

    @Transactional
    public void delete(Long reviewId) {
        MovieReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));

        User currentUser = currentUserService.requireUser();

        // Allow deletion if user is the reviewer or admin
        if (!review.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != com.datvexemphim.domain.enums.Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền xóa review này");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public boolean hasUserReviewed(Long movieId, Long userId) {
        return reviewRepository.existsByMovieIdAndUserId(movieId, userId);
    }

    private MovieReviewDto toDto(MovieReview r) {
        return new MovieReviewDto(
                r.getId(),
                r.getMovie().getId(),
                r.getUser().getId(),
                r.getUser().getFullName(),
                r.getUser().getAvatarUrl(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
