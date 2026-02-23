package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {

        log.info("Попытка создания фильма: " + film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан. id = " + film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyIdException("Id должен быть указан");
        }
        log.info("Попытка обновления фильма id = " + newFilm.getId());

        if (films.containsKey(newFilm.getId())) {

            films.put(newFilm.getId(), newFilm);
            log.info("Фильм успешно обновлён. id = " + newFilm.getId());
            return  newFilm;
        }
        throw new EmptyDataException("Фильм с id = " + newFilm.getId() + " не найден.");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
