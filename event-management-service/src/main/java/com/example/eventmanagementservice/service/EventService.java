package com.example.eventmanagementservice.service;

import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Event createEvent(EventDTO eventDTO);
    Event updateEvent(Long id, EventUpdateDTO eventDto);
    void cancelEvent(Long eventId, String reason);
    void closeRegistration(Long eventId);
    void completeEvent(Long eventId);
    void expandMaxParticipants(Long eventId, int additionalSeats);
    void trimToSize(Long eventId);
    List<Event> getDraftEvents();
    boolean eventExists(Long eventId);
    List<Event> findEventsByIds(List<Long> ids);
    Optional<Event> getEventWithMostParticipants();
    Map<EventStatus, List<Event>> groupEventsByStatus();
    Map<Boolean, List<Event>> partitionEventsByDate();
    List<Event> searchByKeyword(String keyword);
    List<Event> filterByStatusFormatLocation(EventStatus status, EventFormat format, String location);
    List<Event> findEventsInDateRange(String from, String to);
    List<Event> findEventsWithAvailableSeats(Integer minSeats);
    List<Event> getUpcomingEvents();
}
