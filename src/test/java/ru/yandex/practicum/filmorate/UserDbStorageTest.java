package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    public void testSaveAndFindById() {
        User user = createUser("test@mail.com", "login1");
        User savedUser = userStorage.save(user);

        Optional<User> userOptional = userStorage.findById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", savedUser.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("email", "test@mail.com");
                });
    }

    @Test
    public void testFindByEmail() {
        User user = createUser("email@test.com", "login2");
        userStorage.save(user);

        Optional<User> userOptional = userStorage.findByEmail("email@test.com");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("login", "login2"));
    }

    @Test
    public void testFindAll() {
        User user1 = createUser("user1@mail.com", "login1");
        User user2 = createUser("user2@mail.com", "login2");
        userStorage.save(user1);
        userStorage.save(user2);

        List<User> users = userStorage.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testUpdateUser() {
        User user = createUser("old@mail.com", "oldLogin");
        User savedUser = userStorage.save(user);

        savedUser.setName("Updated Name");
        savedUser.setEmail("new@mail.com");
        userStorage.update(savedUser);

        Optional<User> updatedUser = userStorage.findById(savedUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("name", "Updated Name");
                    assertThat(u).hasFieldOrPropertyWithValue("email", "new@mail.com");
                });
    }

    @Test
    public void testFriendsLogic() {
        User user1 = userStorage.save(createUser("u1@mail.com", "u1"));
        User user2 = userStorage.save(createUser("u2@mail.com", "u2"));
        User user3 = userStorage.save(createUser("u3@mail.com", "u3"));

        // Добавление в друзья
        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        // Получение друзей
        List<User> user1Friends = userStorage.getFriends(user1.getId());
        assertThat(user1Friends).hasSize(2).extracting(User::getId).containsExactlyInAnyOrder(user2.getId(), user3.getId());

        // Общие друзья
        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).hasSize(1).extracting(User::getId).containsExactly(user3.getId());

        // Удаление из друзей
        userStorage.removeFriend(user1.getId(), user2.getId());
        user1Friends = userStorage.getFriends(user1.getId());
        assertThat(user1Friends).hasSize(1).extracting(User::getId).containsExactly(user3.getId());
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