package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDto {

    private Long reviewId;
    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;
    @NotNull(message = "Тип отзыва не может быть пустым")
    private Boolean isPositive;
    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;
    @NotNull(message = "ID фильма не может быть пустым")
    private Long filmId;
    private Integer useful;
}
