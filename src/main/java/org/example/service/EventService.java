package org.example.service;

import org.example.dto.EventDTO;
import org.example.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(EventDTO eventDto);
    void publishEvent(Long eventId);
    void cancelEvent(Long eventId);
    List<Event> getUpcomingEvents();
}
