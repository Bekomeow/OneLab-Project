package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(EventDTO eventDto);
    void publishEvent(Long eventId);
    void cancelEvent(Long eventId, String reason);
    List<Event> getUpcomingEvents();
    List<Event> getDraftEvents();
    boolean eventExists(Long eventId);
    List<Event> findEventsByIds(List<Long> ids);
    Optional<Event> getEventWithMostParticipants();
    Map<EventStatus, List<Event>> groupEventsByStatus();
    Map<Boolean, List<Event>> partitionEventsByDate();
}
