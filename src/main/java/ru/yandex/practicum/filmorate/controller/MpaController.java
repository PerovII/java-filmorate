package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService service;

    @GetMapping
    public List<FilmRating> getAll() {
        log.info("Запрос на получение списка возрастных рейтингов");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FilmRating getById(@PathVariable long id) {
        log.info("Запрос на получение названия возрастного рейтинга с id={}", id);
        return service.getById(id);
    }
}
