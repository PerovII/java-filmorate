package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long userId, long friendId) {
        log.info("Попытка добавления пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
        friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);

        log.info("Пользователь с id = {} добавлен в друзья пользователю с id = {}", friendId, userId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        log.info("Попытка удаления пользователя с id = {} из друзей пользователя с id = {}", friendId, userId);

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь с id = {} удалён из друзей пользователя с id = {}", friendId, userId);
        return user;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        log.info("Попытка получения списка общих друзей пользователей с id = {} и id = {}", userId, otherUserId);

        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherUserId);

        List<User> commonFriends = user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .filter(id -> otherUser.getFriends().get(id) == FriendshipStatus.CONFIRMED)
                .map(userStorage::getById)
                .toList();

        log.info("Найдено {} общих друзей у пользователей с id = {} и id = {}",
                commonFriends.size(), userId, otherUserId);

        return commonFriends;
    }

    public List<User> getFriends(long userId) {
        log.info("Попытка получения списка друзей пользователя с id = {}", userId);

        User user = userStorage.getById(userId);

        List<User> friends = user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> userStorage.getById(entry.getKey()))
                .toList();

        log.info("У пользователя с id = {} найдено {} друзей", userId, friends.size());
        return friends;
    }

    public List<User> getAll() {
        log.info("Получение списка всех пользователей");
        return userStorage.getAll();
    }

    public User create(User user) {
        log.info("Создание пользователя: {}", user.getLogin());
        return userStorage.create(user);
    }

    public User update(User user) {
        log.info("Обновление пользователя id = {}", user.getId());
        return userStorage.update(user);
    }

}


