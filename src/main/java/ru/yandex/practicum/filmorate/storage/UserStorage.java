package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    User create(User user);

    User delete(long id);

    User update(User user);

    User getById(long id);

    List<User> getAll();
}
