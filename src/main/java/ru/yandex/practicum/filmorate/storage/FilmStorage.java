package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Optional<Film> findById(long id);

    Film save(Film film);

    Film update(Film film);

    void delete(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopular(int count, Long genreId, Integer year);

    List<Film> getRecommendations(long userId);

    List<Film> searchFilms(String query, String by);

    List<Film> getFilmsByDirector(long directorId, String sortBy);

    List<Film> getCommonFilms(long userId, long friendId);
}