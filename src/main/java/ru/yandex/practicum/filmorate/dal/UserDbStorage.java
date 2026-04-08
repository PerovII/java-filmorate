package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.Optional;

@Repository("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String CHECK_FRIENDSHIP_EXISTS_QUERY = "SELECT count(*) FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id " +
            "WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users u " +
            "JOIN friends f1 ON u.user_id = f1.friend_id " +
            "JOIN friends f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    @Override
    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public User save(User user) {
        long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        Integer count = jdbc.queryForObject(CHECK_FRIENDSHIP_EXISTS_QUERY, Integer.class, userId, friendId);
        if (count == null || count == 0) {
            jdbc.update(INSERT_FRIEND_QUERY, userId, friendId);
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return findMany(GET_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        return findMany(GET_COMMON_FRIENDS_QUERY, userId, otherUserId);
    }
}