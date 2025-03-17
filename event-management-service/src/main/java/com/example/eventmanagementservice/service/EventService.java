package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;

import java.util.List;

public interface EventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(EventDTO eventDto);
    void publishEvent(Long eventId);
    void cancelEvent(Long eventId, String reason);
    List<Event> getUpcomingEvents();
    List<Event> getDraftEvents();
    List<Event> getEventsByTitleAndDescription(String query);
    boolean eventExists(Long eventId);
    List<Event> findEventsByIds(List<Long> ids);
}
