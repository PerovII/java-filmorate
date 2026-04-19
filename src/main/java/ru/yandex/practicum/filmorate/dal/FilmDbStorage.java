package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating_name FROM films f " +
            "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.name AS rating_name FROM films f " +
            "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films " +
            "(name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_GENRES_BY_FILM_ID_QUERY = "SELECT g.genre_id, g.name " +
            "FROM genres g JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
    private static final String GET_DIRECTORS_BY_FILM_ID_QUERY =
            "SELECT d.director_id, d.name FROM directors d " +
                    "JOIN film_directors fd ON d.director_id = fd.director_id " +
                    "WHERE fd.film_id = ?";
    private static final String DELETE_FILM_DIRECTORS_QUERY = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String INSERT_FILM_DIRECTORS_QUERY = "INSERT INTO film_directors (film_id, director_id) " +
            "VALUES (?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        loadGenresForFilms(films);
        loadDirectorsForFilms(films);
        return films;
    }

    @Override
    public Optional<Film> findById(long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(this::loadGenres);
        film.ifPresent(this::loadDirectors);
        return film;
    }

    @Override
    public Film save(Film film) {
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        updateGenres(film);
        updateDirectors(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        updateDirectors(film);
        return film;
    }

    @Override
    public void delete(long id) {
        boolean deleted = delete(DELETE_QUERY, id);
        if (!deleted) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
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
    public List<Film> getPopular(int count, Long genreId, Integer year) {

        StringBuilder sql = new StringBuilder(
                "SELECT f.*, r.name AS rating_name, COUNT(DISTINCT fl.user_id) as likes_count " +
                        "FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "LEFT JOIN film_likes fl ON f.film_id = fl.film_id "
        );

        List<Object> params = new ArrayList<>();

        if (genreId != null) {
            sql.append("LEFT JOIN film_genre fg ON f.film_id = fg.film_id");
        }
        List<String> conditions = new ArrayList<>();

        if (genreId != null) {
            conditions.add("fg.genre_id = ?");
            params.add(genreId);
        }

        if (year != null) {
            conditions.add("YEAR(f.release_date) = ?");
            params.add(year);
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name ");
        sql.append(" ORDER BY likes_count DESC, f.film_id ");
        sql.append(" LIMIT ?");
        params.add(count);

        List<Film> films = jdbc.query(sql.toString(), mapper, params.toArray());

        if (films != null && !films.isEmpty()) {
            loadGenresForFilms(films);
            loadDirectorsForFilms(films);
        }

        return films != null ? films : new ArrayList<>();
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

    private void loadGenresForFilms(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String query = String.format(
                "SELECT fg.film_id, g.genre_id, g.name " +
                        "FROM film_genre fg " +
                        "JOIN genres g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id IN (%s)", inSql);

        Map<Long, List<Genre>> genresByFilmId = new HashMap<>();

        jdbc.query(query, rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));

            genresByFilmId.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        }, filmIds.toArray());

        for (Film film : films) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new ArrayList<>()));
        }
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        String sql = """
                SELECT f.*, r.name AS rating_name
                FROM films f
                LEFT JOIN film_ratings r ON f.rating_id = r.rating_id
                JOIN film_likes fl2 ON f.film_id = fl2.film_id
                WHERE fl2.user_id = (
                    SELECT fl2.user_id
                    FROM film_likes fl1
                    JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
                    WHERE fl1.user_id = ?
                      AND fl2.user_id <> ?
                    GROUP BY fl2.user_id
                    ORDER BY COUNT(*) DESC
                    LIMIT 1
                )
                AND f.film_id NOT IN (
                    SELECT film_id
                    FROM film_likes
                    WHERE user_id = ?
                )
                """;

        List<Film> films = findMany(sql, userId, userId, userId);

        if (films != null && !films.isEmpty()) {
            loadGenresForFilms(films);
            loadDirectorsForFilms(films);
        }

        return films != null ? films : new ArrayList<>();
    }

    private void loadDirectors(Film film) {
        List<Director> directors = jdbc.query(GET_DIRECTORS_BY_FILM_ID_QUERY, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));
            return director;
        }, film.getId());
        film.setDirectors(directors);
    }

    private void loadDirectorsForFilms(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String query = String.format(
                "SELECT fd.film_id, d.director_id, d.name " +
                        "FROM film_directors fd " +
                        "JOIN directors d ON fd.director_id = d.director_id " +
                        "WHERE fd.film_id IN (%s)", inSql);

        Map<Long, List<Director>> directorsByFilmId = new HashMap<>();

        jdbc.query(query, rs -> {
            long filmId = rs.getLong("film_id");
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));
            directorsByFilmId.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
        }, filmIds.toArray());

        for (Film film : films) {
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), new ArrayList<>()));
        }
    }

    private void updateDirectors(Film film) {
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Object[]> batch = film.getDirectors().stream()
                    .map(Director::getId)
                    .distinct()
                    .map(directorId -> new Object[]{film.getId(), directorId})
                    .toList();
            jdbc.batchUpdate(INSERT_FILM_DIRECTORS_QUERY, batch);
        }
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        String getFilmsByDirectorSortByYear =
                "SELECT f.*, r.name AS rating_name FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "INNER JOIN film_directors fd ON f.film_id = fd.film_id " +
                        "WHERE fd.director_id = ? " +
                        "ORDER BY f.release_date";

        String getFilmsByDirectorSortByLikes =
                "SELECT f.*, r.name AS rating_name, COUNT(fl.user_id) as likes_count FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "INNER JOIN film_directors fd ON f.film_id = fd.film_id " +
                        "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                        "WHERE fd.director_id = ? " +
                        "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                        "ORDER BY likes_count DESC";

        List<Film> films;

        if ("year".equalsIgnoreCase(sortBy)) {
            films = findMany(getFilmsByDirectorSortByYear, directorId);
        } else {
            films = findMany(getFilmsByDirectorSortByLikes, directorId);
        }

        if (films != null && !films.isEmpty()) {
            loadGenresForFilms(films);
            loadDirectorsForFilms(films);
        }

        return films != null ? films : new ArrayList<>();
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        List<Film> films;

        String normalizedBy = by.toLowerCase().trim().replaceAll("\\s+", "");

        String searchByTitle =
                "SELECT f.*, r.name AS rating_name FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "WHERE LOWER(f.name) LIKE ?";

        String searchByDirector =
                "SELECT DISTINCT f.*, r.name AS rating_name FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "INNER JOIN film_directors fd ON f.film_id = fd.film_id " +
                        "INNER JOIN directors d ON fd.director_id = d.director_id " +
                        "WHERE LOWER(d.name) LIKE ?";

        String searchByBoth =
                "SELECT DISTINCT f.*, r.name AS rating_name FROM films f " +
                        "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                        "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                        "WHERE LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ?";

        if (normalizedBy.equals("title")) {
            films = findMany(searchByTitle, searchPattern);
        } else if (normalizedBy.equals("director")) {
            films = findMany(searchByDirector, searchPattern);
        } else if (normalizedBy.equals("title,director") || normalizedBy.equals("director,title")) {
            films = findMany(searchByBoth, searchPattern, searchPattern);
        } else {
            throw new IllegalArgumentException("Invalid search parameter: " + by);
        }

        if (films != null && !films.isEmpty()) {
            loadGenresForFilms(films);
            loadDirectorsForFilms(films);
        }

        return films != null ? films : new ArrayList<>();
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT f.*, r.name AS rating_name, COUNT(DISTINCT fl.user_id) AS likes_count " +
                "FROM films f " +
                "JOIN film_likes fl1 ON f.film_id = fl1.film_id " +
                "JOIN film_likes fl2 ON f.film_id = fl2.film_id " +
                "LEFT JOIN film_ratings r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "WHERE fl1.user_id = ? AND fl2.user_id = ? " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                "ORDER BY likes_count DESC";

        List<Film> films = findMany(sql, userId, friendId);

        if (films != null && !films.isEmpty()) {
            loadGenresForFilms(films);
            loadDirectorsForFilms(films);
        }

        return films != null ? films : new ArrayList<>();
    }
}