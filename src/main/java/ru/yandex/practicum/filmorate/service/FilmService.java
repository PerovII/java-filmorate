package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final EventService eventService;
    private final DirectorStorage directorStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.eventService = eventService;
        this.directorStorage = directorStorage;
    }

    public List<FilmDto> getAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(long filmId) {
        Film film = filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto create(NewFilmRequest request) {
        validateMpaAndGenres(request.getMpa().getId(), request.getGenres());

        Film film = FilmMapper.mapToFilm(request);
        film = filmStorage.save(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film film = filmStorage.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        validateMpaAndGenres(request.getMpa().getId(), request.getGenres());

        Film updatedFilm = FilmMapper.updateFilmFields(film, request);
        updatedFilm = filmStorage.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        filmStorage.addLike(filmId, userId);
        eventService.addEvent(userId, Event.EventType.LIKE, Event.Operation.ADD, filmId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        filmStorage.removeLike(filmId, userId);
        eventService.addEvent(userId, Event.EventType.LIKE, Event.Operation.REMOVE, filmId);
    }

    public List<FilmDto> getPopularFilms(int count, Long genreId, Integer year) {
        if (count < 1) {
            throw new IllegalArgumentException("Количество популярных фильмов(count) должно быть больше 0.");
        }
        if (genreId != null) {
            genreStorage.findById(genreId)
                    .orElseThrow(() -> new NotFoundException("Жанр не найден с id " + genreId));
        }

        return filmStorage.getPopular(count, genreId, year).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateMpaAndGenres(Long mpaId, List<Genre> genres) {
        if (mpaId != null) {
            mpaStorage.findById(mpaId)
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден"));
        }
        if (genres != null) {
            for (Genre genre : genres) {
                genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
            }
        }
    }

    public void delete(long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        filmStorage.delete(filmId);
        log.info("Фильм id={} успешно удален", filmId);
    }

    public List<FilmDto> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Поисковый запрос не может быть пуст");
        }
        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("Параметр 'by' не может быть пуст");
        }

        String normalizedBy = by.toLowerCase().trim().replaceAll("\\s+", "");

        if (!normalizedBy.equals("title") && !normalizedBy.equals("director") && !normalizedBy.equals("title,director")
                && !normalizedBy.equals("director,title")) {
            throw new IllegalArgumentException("Параметр 'by' может принимать значения: " +
                    "title, director");
        }

        return filmStorage.searchFilms(query, by).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsByDirector(long directorId, String sortBy) {
        directorStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден с id=" + directorId));

        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);

        log.info("Найдено фильмов для режиссёра {}: {}", directorId, films.size());

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь (друг) с id " + friendId + " не найден"));

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        log.info("Найдено общих фильмов: {}", commonFilms.size());

        return commonFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}