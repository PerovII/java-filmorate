package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EmptyDataException;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User create(User user) {
        normalizeName(user);
        log.info("Попытка создания пользователя: " + user.getName());
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Пользователь создан. id = " + user.getId());
        return user;
    }

    @Override
    public User delete(long id) {
        log.info("Попытка удаления пользователя id = {}", id);

        User user = users.remove(id);
        if (user == null) {
            throw new EmptyDataException("Пользователь с id = " + id + " не найден.");
        }

        log.info("Пользователь с id = {} успешно удалён", id);
        return user;
    }

    @Override
    public User update(User newUser) {
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

    @Override
    public User getById(long id) {
        log.info("Попытка получения пользователя id = {}", id);

        User user = users.get(id);
        if (user == null) {
            throw new EmptyDataException("Пользователь с id = " + id + " не найден.");
        }

        log.info("Пользователь с id = {} успешно найден", id);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
