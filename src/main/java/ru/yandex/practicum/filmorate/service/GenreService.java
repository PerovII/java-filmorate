package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.List;

@Service
public class GenreService {
    private final GenreStorage storage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage storage) {
        this.storage = storage;
    }

    public List<Genre> getAll() {
        return storage.findAll();
    }

    public Genre getById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }
}
