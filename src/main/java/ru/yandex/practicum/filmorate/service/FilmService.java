package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
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
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        filmStorage.removeLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Параметр count должен быть больше 0.");
        }
        return filmStorage.getPopular(count).stream()
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
}