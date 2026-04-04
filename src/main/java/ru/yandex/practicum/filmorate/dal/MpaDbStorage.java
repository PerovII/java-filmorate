package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import java.util.List;
import java.util.Optional;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<FilmRating> mapper;

    @Override
    public List<FilmRating> findAll() {
        return jdbc.query("SELECT * FROM film_ratings ORDER BY rating_id ASC", mapper);
    }

    @Override
    public Optional<FilmRating> findById(long id) {
        String sql = "SELECT * FROM film_ratings WHERE rating_id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }
}