package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            EventService eventService
    ) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userStorage.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        User user = userStorage.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        User updatedUser = UserMapper.updateUserFields(user, request);
        updatedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(long userId, long friendId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден"));
        userStorage.addFriend(userId, friendId);

        // Добавляем событие о добавлении в друзья
        eventService.addEvent(userId, Event.EventType.FRIEND, Event.Operation.ADD, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден"));
        userStorage.removeFriend(userId, friendId);

        // Добавляем событие об удалении из друзей
        eventService.addEvent(userId, Event.EventType.FRIEND, Event.Operation.REMOVE, friendId);
    }

    public List<UserDto> getFriends(long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(long userId, long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getRecommendations(Long userId) {

        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<FilmDto> filmDtoList = filmStorage.getRecommendations(userId).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
        log.info("Найдено рекомендованных фильмов {} для пользователя с id {}", filmDtoList.size(), userId);
        return filmDtoList;
    }

    public void delete(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userStorage.delete(userId);
        log.info("Пользователь id={} успешно удален", userId);
    }
}