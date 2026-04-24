package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> findAll();

    Optional<Director> findById(long id);

    Director save(Director director);

    Director update(Director director);

    void delete(long id);

    List<Director> getDirectorsByFilmId(long filmId);

    void addDirectorToFilm(long filmId, long directorId);

    void removeDirectorsFromFilm(long filmId);

    List<Director> findAllById(List<Long> id);

}