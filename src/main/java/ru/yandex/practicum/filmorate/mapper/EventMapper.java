package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .userId(event.getUserId())
                .eventType(event.getEventType().name())
                .operation(event.getOperation().name())
                .entityId(event.getEntityId())
                .timestamp(event.getTimestamp())
                .build();
    }
}