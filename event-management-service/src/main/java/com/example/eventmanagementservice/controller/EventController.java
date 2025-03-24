package com.example.eventmanagementservice.controller;

import com.example.commonlibrary.dto.event.CancelEventRequest;
import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventSearchService eventSearchService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventUpdateDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, eventDTO));
    }

    //FOR MODERATOR ONLY
    @PostMapping("/{eventId}/publish")
    public ResponseEntity<Void> publishEvent(@PathVariable Long eventId) {
        eventService.publishEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    //FOR MODERATOR AND EVENT OWNER
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long eventId,
                                            @RequestBody CancelEventRequest request) {
        eventService.cancelEvent(eventId, request.getReason());
        return ResponseEntity.noContent().build();
    }

    //FOR EVENT OWNER
    @PostMapping("/{eventId}/complete")
    public ResponseEntity<Void> completeEvent(@PathVariable Long eventId) {
        eventService.completeEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    //FOR EVENT OWNER
    @PostMapping("/{eventId}/expand")
    public ResponseEntity<Void> expandMaxParticipants(@PathVariable Long eventId, @RequestParam int additionalSeats) {
        eventService.expandMaxParticipants(eventId, additionalSeats);
        return ResponseEntity.noContent().build();
    }

    //FOR EVENT OWNER
    @PostMapping("/{eventId}/trim-to-size")
    public ResponseEntity<Void> trimToSize(@PathVariable Long eventId) {
        eventService.trimToSize(eventId);
        return ResponseEntity.noContent().build();
    }

    //FOR EVENT OWNER
    @PostMapping("/{eventId}/close-registration")
    public ResponseEntity<Void> closeEventRegistration(@PathVariable Long eventId) {
        eventService.closeRegistration(eventId);
        return ResponseEntity.noContent().build();
    }

    //FOR MODERATOR ONLY
    @GetMapping("/drafts")
    public ResponseEntity<List<Event>> getDraftEvents() {
        return ResponseEntity.ok(eventService.getDraftEvents());
    }

    @GetMapping("/stream/most-popular")
    public ResponseEntity<Event> getMostPopularEvent() {
        return ResponseEntity.of(eventService.getEventWithMostParticipants());
    }

    @GetMapping("/stream/grouped")
    public ResponseEntity<Map<EventStatus, List<Event>>> getGroupedEvents() {
        return ResponseEntity.ok(eventService.groupEventsByStatus());
    }

    @GetMapping("/stream/partitioned")
    public ResponseEntity<Map<Boolean, List<Event>>> getPartitionedEvents() {
        return ResponseEntity.ok(eventService.partitionEventsByDate());
    }


    // === CATALOG METHODS ===

    @GetMapping("/catalog/search")
    public ResponseEntity<List<Event>> searchByKeywordCatalog(@RequestParam String keyword) {
        return ResponseEntity.ok(eventService.searchByKeyword(keyword));
    }

    @GetMapping("/catalog/filter")
    public ResponseEntity<List<Event>> filterByStatusFormatLocationCatalog(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventFormat format,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(eventService.filterByStatusFormatLocation(status, format, location));
    }

    @GetMapping("/catalog/date-range")
    public ResponseEntity<List<Event>> findEventsInDateRangeCatalog(
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return ResponseEntity.ok(eventService.findEventsInDateRange(from, to));
    }

    @GetMapping("/catalog/available-seats")
    public ResponseEntity<List<Event>> findEventsWithAvailableSeatsCatalog(@RequestParam int minSeats) {
        return ResponseEntity.ok(eventService.findEventsWithAvailableSeats(minSeats));
    }

    @GetMapping("/catalog/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEventsCatalog() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/catalog/aggregation")
    public ResponseEntity<Object> getEventsPerDateAggregationCatalog() {
        return ResponseEntity.ok(eventService.getEventsPerDateAggregation());
    }

    @GetMapping("/catalog/popular")
    public ResponseEntity<List<Event>> getMostPopularEventsCatalog() {
        return ResponseEntity.ok(eventService.getMostPopularEvents());
    }
}

