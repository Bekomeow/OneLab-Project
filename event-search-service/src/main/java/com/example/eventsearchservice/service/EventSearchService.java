package com.example.eventsearchservice.service;

import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;

import java.time.Instant;
import java.util.List;

public interface EventSearchService {
    void saveEvent(EventSearchDto eventDto);
    void updateEvent(EventSearchDto eventDto);
    void deleteEvent(Long eventId);
    List<Long> searchByKeyword(String keyword);
    List<Long> filterByStatusFormatLocation(EventStatus status, EventFormat format, String location);
    List<Long> findEventsInDateRange(Instant from, Instant to);
    List<Long> findEventsWithAvailableSeats(Integer minSeats);
    List<Long> getUpcomingEvents();
}
