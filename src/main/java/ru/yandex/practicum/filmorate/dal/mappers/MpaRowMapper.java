package ru.yandex.practicum.filmorate.dal.mappers;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<FilmRating> {
    @Override
    public FilmRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmRating mpa = new FilmRating();
        mpa.setId(rs.getLong("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
