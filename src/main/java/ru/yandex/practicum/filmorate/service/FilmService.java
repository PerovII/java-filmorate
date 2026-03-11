package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(long filmId, long userId) {

        log.info("Попытка добавления лайка фильму с id = {}.", filmId);
        userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (film.getLikes().add(userId)) {
            log.info("Лайк пользователя {} добавлен фильму {}", userId, filmId);
        } else {
            log.warn("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
        }
        return film;
    }

    public Film removeLike(long filmId, long userId) {

        log.info("Попытка удаления лайка с фильма с id = {}.", filmId);
        userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (!film.getLikes().remove(userId)) {
            log.warn("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        } else {
        log.info("Лайк пользователя с id = {} успешно удален.", userId);
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Попытка получения списка первых {} популярных фильмов", count);
        if (count < 1) {
            throw new ValidationException("Параметр count должен быть больше 0.");
        }
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAll() {
        log.info("Получение списка всех фильмов");
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        log.info("Создание фильма: {}", film.getName());
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Обновление фильма id = {}", film.getId());
        return filmStorage.update(film);
    }
}
