package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> getAll() {
        log.info("Запрос на получение всех режиссёров");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getById(@PathVariable long id) {
        log.info("Запрос на получение режиссёра с id = {}", id);
        return directorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody DirectorDto directorDto) {
        log.info("Запрос на создание режиссёра: {}", directorDto.getName());
        return directorService.create(directorDto);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto directorDto) {
        log.info("Запрос на обновление режиссёра id = {}", directorDto.getId());
        return directorService.update(directorDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление режиссёра id = {}", id);
        directorService.delete(id);
    }
}