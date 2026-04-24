package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {
    public static ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review toModel(ReviewDto dto) {
        Review review = new Review();
        review.setReviewId(dto.getReviewId());
        review.setContent(dto.getContent());
        review.setIsPositive(dto.getIsPositive());
        review.setUserId(dto.getUserId());
        review.setFilmId(dto.getFilmId());
        review.setUseful(dto.getUseful() == null ? 0 : dto.getUseful());
        return review;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest request) {
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasIsPositive()) {
            review.setIsPositive(request.getIsPositive());
        }
        if (request.hasUserId()) {
            review.setUserId(request.getUserId());
        }
        if (request.hasFilmId()) {
            review.setFilmId(request.getFilmId());
        }
        if (request.hasUseful()) {
            review.setUseful(request.getUseful());
        }
        return review;
    }
}
