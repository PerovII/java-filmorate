package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    User update(User user);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherUserId);
}