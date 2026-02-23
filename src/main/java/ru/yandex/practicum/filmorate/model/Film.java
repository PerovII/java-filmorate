package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.validation.MinDate;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {

    private Long id;

    @MinDate(message = "Дата не может быть раньше 28.12.1895 г.")
    private LocalDate releaseDate;

    @NotBlank(message = "Название не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private int duration;
}
