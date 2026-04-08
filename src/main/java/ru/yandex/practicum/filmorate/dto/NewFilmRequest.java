package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validation.MinDate;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    @MinDate(message = "Дата не может быть раньше 28.12.1895 г.")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private int duration;

    private Mpa mpa;
    private List<Genre> genres;
}