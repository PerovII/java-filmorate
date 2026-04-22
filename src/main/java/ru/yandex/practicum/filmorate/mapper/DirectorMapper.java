package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

public class DirectorMapper {

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }

    public static Director mapToDirector(DirectorDto dto) {
        Director director = new Director();
        director.setId(dto.getId());
        director.setName(dto.getName());
        return director;
    }
}
