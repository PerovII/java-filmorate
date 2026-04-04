package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        }
        film.setDuration(rs.getInt("duration"));

        FilmRating mpa = new FilmRating();
        mpa.setId(rs.getLong("rating_id"));
        // Имя рейтинга должно извлекаться через JOIN в запросе
        try {
            mpa.setName(rs.getString("rating_name"));
        } catch (SQLException ignored) {
            // Если колонка отсутствует в конкретном запросе
        }
        film.setMpa(mpa);

        return film;
    }
}