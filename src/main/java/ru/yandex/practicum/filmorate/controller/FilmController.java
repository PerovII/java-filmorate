package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> getAll() {
        log.info("Запрос на получение всех фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable long filmId) {
        log.info("Запрос на получение фильма id={}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        log.info("Запрос на создание фильма: {}", request.getName());
        return filmService.create(request);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Запрос на обновление фильма id = {}", request.getId());
        return filmService.update(request);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос на добавление лайка, фильм id = {}, пользователь id = {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос на удаление лайка, фильм id = {}, пользователь id = {}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос списка первых {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(
            @RequestParam String query,
            @RequestParam String by) {
        log.info("Поиск фильмов: query='{}', by='{}'", query, by);
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(
            @PathVariable long directorId,
            @RequestParam(required = false) String sortBy) {
        log.info("Запрос на получение фильмов режиссёра id={}, sortBy={}", directorId, sortBy);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}