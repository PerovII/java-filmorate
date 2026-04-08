package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
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

        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("rating_id"));
        try {
            mpa.setName(rs.getString("rating_name"));
        } catch (SQLException exception) {
            log.warn("Колонка 'rating_name' отсутствует в ResultSet. " +
                    "Название рейтинга MPA не заполнено для фильма с id = {}", film.getId());
        }
        film.setMpa(mpa);

        return film;
    }
}