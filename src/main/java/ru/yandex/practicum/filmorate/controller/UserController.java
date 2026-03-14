package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid  @RequestBody User user) {
        log.info("Получен запрос на создание нового пользователя {}", user.getName());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление данных пользователя id = {}", newUser.getId());
        return userService.update(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Получен запрос на добавление пользователя id = {} в друзья к пользователю id = {}",
                friendId, userId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Получен запрос на удаление пользователя id = {} из друзей пользователя id = {}",
                friendId, userId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable long userId) {
        log.info("Получен запрос на получение списка друзей пользователя id = {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) {
        log.info("Получен запрос на получение списка общих друзей между пользователями с id = {} и id = {}",
                userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }



}
