package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film delete(long id);

    Film update(Film film);

    Film getById(long id);

    List<Film> getAll();
}
