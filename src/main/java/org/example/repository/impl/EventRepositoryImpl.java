package org.example.repository.impl;

import org.example.dto.EventDTO;
import org.example.repository.EventRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EventRepositoryImpl implements EventRepository {
    private final Map<Long, EventDTO> events = new HashMap<>();

    @Override
    public Optional<EventDTO> findById(Long id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public List<EventDTO> findAll() {
        return new ArrayList<>(events.values());
    }

    @Override
    public Optional<EventDTO> save(EventDTO event) {
        event.setId((long) (events.size() + 1));
        events.put(event.getId(), event);
        return Optional.ofNullable(event);
    }
}
