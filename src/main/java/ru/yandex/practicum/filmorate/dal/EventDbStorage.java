package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Repository("eventDbStorage")
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbc;
    private final RowMapper<Event> mapper;

    private static final String GET_USER_EVENTS_QUERY =
            "SELECT * FROM user_events WHERE user_id = ? ORDER BY timestamp ASC";

    private static final String INSERT_EVENT_QUERY =
            "INSERT INTO user_events (user_id, event_type, operation, entity_id, timestamp) " +
                    "VALUES (?, ?, ?, ?, ?)";

    @Override
    public List<Event> getUserEvents(long userId) {
        return jdbc.query(GET_USER_EVENTS_QUERY, mapper, userId);
    }

    @Override
    public void addEvent(Event event) {
        jdbc.update(INSERT_EVENT_QUERY,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp()
        );
    }
}