package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.ArrayList;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }

        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream()
                    .map(g -> {
                        Genre genre = new Genre();
                        genre.setId(g.getId());
                        return genre;
                    })
                    .collect(Collectors.toList()));
        }

        if (request.getDirectors() != null) {
            film.setDirectors(request.getDirectors().stream()
                    .map(d -> {
                        Director director = new Director();
                        director.setId(d.getId());
                        director.setName(d.getName());
                        return director;
                    })
                    .collect(Collectors.toList()));
        }

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(MpaMapper.mapToMpaDto(film.getMpa()));
        }

        if (film.getGenres() != null) {
            dto.setGenres(film.getGenres().stream()
                    .map(GenreMapper::mapToGenreDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setGenres(new ArrayList<>());
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            dto.setDirectors(film.getDirectors().stream()
                    .map(DirectorMapper::mapToDirectorDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setDirectors(new ArrayList<>());
        }

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }

        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream()
                    .map(g -> {
                        Genre genre = new Genre();
                        genre.setId(g.getId());
                        return genre;
                    })
                    .collect(Collectors.toList()));
        }

        if (request.getDirectors() != null) {
            film.setDirectors(request.getDirectors().stream()
                    .map(d -> {
                        Director director = new Director();
                        director.setId(d.getId());
                        director.setName(d.getName());
                        return director;
                    })
                    .collect(Collectors.toList()));
        } else {
            film.setDirectors(new ArrayList<>());
        }

        return film;
    }
}