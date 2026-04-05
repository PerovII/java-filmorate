package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    public void testFindAll() {
        List<FilmRating> ratings = mpaStorage.findAll();

        assertThat(ratings).isNotEmpty();
        assertThat(ratings).hasSize(5);
    }

    @Test
    public void testFindById() {
        Optional<FilmRating> ratingOptional = mpaStorage.findById(1);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating -> {
                    assertThat(rating).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(rating).hasFieldOrPropertyWithValue("name", "G");
                });
    }
}