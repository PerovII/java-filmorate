package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository("directorDbStorage")
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String GET_DIRECTORS_BY_FILM_QUERY  = "SELECT d.* FROM directors d " +
            "JOIN film_directors fd ON d.director_id = fd.director_id " +
            "WHERE fd.film_id = ?";
    private static final String ADD_DIRECTOR_TO_FILM  = "INSERT INTO film_directors (film_id, director_id) " +
                                                            "VALUES (?, ?)";
    private static final String REMOVE_DIRECTORS_FROM_FILM  = "DELETE FROM film_directors WHERE film_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Director save(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            director.setId(key.longValue());
        } else {
            throw new RuntimeException("Не удалось сохранить режиссёра");
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(long id) {
        String deleteRelationsQuery = "DELETE FROM film_directors WHERE director_id = ?";
        int relationsDeleted = jdbc.update(deleteRelationsQuery, id);
        int deleted = jdbc.update(DELETE_QUERY, id);
        if (deleted == 0) {
            throw new NotFoundException("Режиссёр с id=" + id + " не найден");
        }
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        return findMany(GET_DIRECTORS_BY_FILM_QUERY, filmId);
    }

    @Override
    public void removeDirectorsFromFilm(long filmId) {
        jdbc.update(REMOVE_DIRECTORS_FROM_FILM, filmId);
    }

    @Override
    public void addDirectorToFilm(long filmId, long directorId) {
        jdbc.update(ADD_DIRECTOR_TO_FILM, filmId, directorId);
    }
}
