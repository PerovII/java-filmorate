package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public Film create(Film film) {
        log.info("Попытка создания фильма: " + film.getName());
        film.setId(idGenerator.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Фильм создан. id = " + film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyIdException("Id должен быть указан");
        }
        log.info("Попытка обновления фильма id = " + newFilm.getId());

        if (films.containsKey(newFilm.getId())) {

            films.put(newFilm.getId(), newFilm);
            log.info("Фильм успешно обновлён. id = " + newFilm.getId());
            return  newFilm;
        }
        throw new EmptyDataException("Фильм с id = " + newFilm.getId() + " не найден.");
    }

    @Override
    public Film delete(long id) {
        Film film;
        log.info("Попытка удаления фильма id = " + id);

        if (films.containsKey(id)) {

            film = films.remove(id);
            log.info("Фильм с id= " + id + " успешно удален");
            return  film;
        }
        throw new EmptyDataException("Фильм с id = " + id + " не найден.");
    }

    @Override
    public Film getById(long id) {
        log.info("Попытка получения фильма id = {}", id);

        Film film = films.get(id);
        if (film == null) {
            throw new EmptyDataException("Фильм с id = " + id + " не найден.");
        }

        log.info("Фильм с id = {} успешно найден", id);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
