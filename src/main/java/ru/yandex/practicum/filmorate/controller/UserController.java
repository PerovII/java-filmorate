package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid  @RequestBody User user) {
        normalizeName(user);
        log.info("Попытка создания пользователя: " + user.getName());
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Пользователь создан. id = " + user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        normalizeName(newUser);
        if (newUser.getId() == null) {
            throw new EmptyIdException("Id должен быть указан");
        }
        log.info("Попытка обновления пользователя id = " + newUser.getId());
        if (users.containsKey(newUser.getId())) {

            users.put(newUser.getId(), newUser);
            log.info("Пользователь успешно обновлён. id = " + newUser.getId());
            return  newUser;
        }
        throw new EmptyDataException("Пользователь с id = " + newUser.getId() + " не найден.");
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
