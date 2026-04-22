package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> mapper;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT * FROM genres", mapper);
    }

    @Override
    public Optional<Genre> findById(long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    @Override
    public List<Genre> findAllById(List<Long> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(id.size(), "?"));
        String query = String.format("SELECT * FROM genres WHERE genre_id IN (%s)", inSql);

        return jdbc.query(query, mapper, id.toArray());
    }
}
