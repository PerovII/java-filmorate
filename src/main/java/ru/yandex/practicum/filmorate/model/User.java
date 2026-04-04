package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}