package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<DirectorDto> getAll() {
        return directorStorage.findAll().stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getById(long id) {
        Director director = directorStorage.findById(id).orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto create(DirectorDto directorDto) {
        Director director = new Director();
        director.setName(directorDto.getName());
        director = directorStorage.save(director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto update(DirectorDto directorDto) {
        directorStorage.findById(directorDto.getId()).orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
        Director director = new Director();
        director.setId(directorDto.getId());
        director.setName(directorDto.getName());
        director = directorStorage.update(director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public void delete(long id) {
        directorStorage.findById(id).orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
        directorStorage.delete(id);
    }

    public List<DirectorDto> getDirectorsByFilmId(long filmId) {
        return directorStorage.getDirectorsByFilmId(filmId).stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }
}