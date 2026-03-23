package com.datvexemphim.api.dto.publicapi;

public class CreateReviewRequest {
    private Long movieId;
    private Integer rating;
    private String comment;

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
