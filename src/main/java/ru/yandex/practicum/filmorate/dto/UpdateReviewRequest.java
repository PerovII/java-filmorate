package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class UpdateReviewRequest {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;

    public boolean hasContent() {
        return !(content == null || content.isBlank());
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasFilmId() {
        return filmId != null;
    }

    public boolean hasUseful() {
        return useful != null;
    }
}
