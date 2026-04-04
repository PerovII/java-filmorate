package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating_name FROM films f LEFT JOIN film_ratings r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.name AS rating_name FROM films f LEFT JOIN film_ratings r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";

    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_QUERY = "SELECT f.*, r.name AS rating_name FROM films f LEFT JOIN film_ratings r ON f.rating_id = r.rating_id LEFT JOIN film_likes fl ON f.film_id = fl.film_id GROUP BY f.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";

    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_GENRES_BY_FILM_ID_QUERY = "SELECT g.genre_id, g.name FROM genres g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Optional<Film> findById(long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(this::loadGenres);
        return film;
    }

    @Override
    public Film save(Film film) {
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        return film;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> films = findMany(GET_POPULAR_QUERY, count);
        films.forEach(this::loadGenres);
        return films;
    }

    private void updateGenres(Film film) {
        jdbc.update(DELETE_FILM_GENRE_QUERY, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Object[]> batch = film.getGenres().stream()
                    .map(genre -> genre.getId())
                    .distinct()
                    .map(genreId -> new Object[]{film.getId(), genreId})
                    .toList();
            jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, batch);
        }
    }

    private void loadGenres(Film film) {
        List<Genre> genres = jdbc.query(GET_GENRES_BY_FILM_ID_QUERY, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(genres);
    }
}