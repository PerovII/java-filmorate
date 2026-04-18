package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    public void testSaveAndFindById() {
        Film film = createFilm("Matrix", "Description", LocalDate.of(1999, 3, 31), 136, 4L);
        Film savedFilm = filmStorage.save(film);

        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", savedFilm.getId());
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Matrix");
                    assertThat(f.getMpa()).hasFieldOrPropertyWithValue("id", 4L);
                });
    }

    @Test
    public void testFindAll() {
        filmStorage.save(createFilm("Film 1", "Desc 1", LocalDate.now(), 100, 1L));
        filmStorage.save(createFilm("Film 2", "Desc 2", LocalDate.now(), 120, 2L));

        List<Film> films = filmStorage.findAll();

        assertThat(films).isNotEmpty();
        assertThat(films.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testUpdateFilm() {
        Film film = createFilm("Old Name", "Old Desc", LocalDate.now(), 100, 1L);
        Film savedFilm = filmStorage.save(film);

        savedFilm.setName("New Name");
        savedFilm.setDescription("New Desc");
        filmStorage.update(savedFilm);

        Optional<Film> updatedFilm = filmStorage.findById(savedFilm.getId());

        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("name", "New Name");
                    assertThat(f).hasFieldOrPropertyWithValue("description", "New Desc");
                });
    }

    @Test
    public void testLikesAndPopular() {
        Film film1 = filmStorage.save(createFilm("Pop 1", "D", LocalDate.now(), 100, 1L));
        Film film2 = filmStorage.save(createFilm("Pop 2", "D", LocalDate.now(), 100, 1L));

        User user1 = userStorage.save(createUser("u1@m.com", "l1"));
        User user2 = userStorage.save(createUser("u2@m.com", "l2"));

        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film1.getId(), user1.getId());

        List<Film> popular = filmStorage.getPopular(10, null, null);

        assertThat(popular).isNotEmpty();
        assertThat(popular.get(0).getId()).isEqualTo(film2.getId()); // У film2 больше лайков

        filmStorage.removeLike(film2.getId(), user1.getId());
        filmStorage.removeLike(film2.getId(), user2.getId());

        popular = filmStorage.getPopular(10, null, null);
        assertThat(popular.get(0).getId()).isEqualTo(film1.getId()); // Теперь у film1 больше лайков
    }

    private Film createFilm(String name, String desc, LocalDate release, int duration, long mpaId) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(desc);
        film.setReleaseDate(release);
        film.setDuration(duration);

        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1L);
        film.setGenres(List.of(genre));

        return film;
    }

    private User createUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}