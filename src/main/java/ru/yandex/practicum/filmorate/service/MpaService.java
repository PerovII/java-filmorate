package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;


import java.util.List;

@Service
public class MpaService {
    private final MpaStorage storage;

    public MpaService(@Qualifier("mpaDbStorage") MpaStorage storage) {
        this.storage = storage;
    }

    public List<FilmRating> getAll() {
        return storage.findAll();
    }

    public FilmRating getById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}