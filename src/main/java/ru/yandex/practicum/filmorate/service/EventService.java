package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    @Qualifier("eventDbStorage")
    private final EventStorage eventStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<EventDto> getUserEvents(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<Event> events = eventStorage.getUserEvents(userId);

        return events.stream()
                .map(EventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

    public void addEvent(long userId, Event.EventType eventType, Event.Operation operation, long entityId) {
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .timestamp(System.currentTimeMillis())
                .build();

        eventStorage.addEvent(event);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}