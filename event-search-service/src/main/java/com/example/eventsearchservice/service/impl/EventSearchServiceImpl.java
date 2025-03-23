package com.example.eventsearchservice.service.impl;

import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.model.EventDocument;
import com.example.eventsearchservice.repository.EventSearchRepository;
import com.example.eventsearchservice.service.EventSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {

    private final EventSearchRepository eventSearchRepository;

    public void saveEvent(EventSearchDto eventDto) {
        EventDocument event = EventDocument.builder()
                .id(eventDto.getEventId().toString())
                .eventId(eventDto.getEventId())
                .title(eventDto.getTitle())
                .description(eventDto.getDescription())
                .location(eventDto.getLocation())
                .eventFormat(eventDto.getEventFormat())
                .maxParticipants(eventDto.getMaxParticipants())
                .availableSeats(eventDto.getAvailableSeats())
                .startDate(eventDto.getStartDate())
                .endDate(eventDto.getEndDate())
                .build();
        eventSearchRepository.save(event);
    }

    public void updateEvent(EventSearchDto eventDto) {
        eventSearchRepository.findByEventId(eventDto.getEventId()).ifPresent(event -> {
            Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
            Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
            Optional.ofNullable(eventDto.getStatus()).ifPresent(event::setStatus);
            Optional.ofNullable(eventDto.getAvailableSeats()).ifPresent(event::setAvailableSeats);
            eventSearchRepository.save(event);
        });
    }

    public void deleteEvent(Long eventId) {
        eventSearchRepository.deleteByEventId(eventId);
    }

    public List<Long> searchByKeyword(String keyword) {
        return eventSearchRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword)
                .stream().map(EventDocument::getEventId)
                .collect(Collectors.toList());
    }

    public List<Long> filterByStatusFormatLocation(EventStatus status, EventFormat format, String location) {
        return eventSearchRepository.findByDynamicFilters(status, format, location)
                .stream()
                .map(EventDocument::getEventId)
                .toList();
    }

    public List<Long> findEventsInDateRange(Instant from, Instant to) {
        return eventSearchRepository.findByStartDateBetween(from, to)
                .stream().map(EventDocument::getEventId)
                .collect(Collectors.toList());
    }

    public List<Long> findEventsWithAvailableSeats(int minSeats) {
        return eventSearchRepository.findByAvailableSeatsGreaterThan(minSeats)
                .stream().map(EventDocument::getEventId)
                .collect(Collectors.toList());
    }

    public List<Long> getUpcomingEvents() {
        return eventSearchRepository.findByStartDateAfterOrderByStartDateAsc(Instant.now())
                .stream()
                .map(EventDocument::getEventId)
                .toList();
    }

    public Object getEventsPerDateAggregation() {
        return eventSearchRepository.countEventsPerDate();
    }

    public List<Long> getMostPopularEvents() {
        return eventSearchRepository.findMostPopularEvents()
                .stream().map(EventDocument::getEventId)
                .collect(Collectors.toList());
    }
}

