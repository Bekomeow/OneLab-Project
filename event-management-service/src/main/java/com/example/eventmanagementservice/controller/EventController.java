package com.example.eventmanagementservice.controller;

import com.example.commonlibrary.dto.event.CancelEventRequest;
import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @PostMapping("/{eventId}/close-registration")
    public ResponseEntity<Void> closeEventRegistration(@PathVariable Long eventId) {
        eventService.closeRegistration(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    //FOR MODERATOR ONLY
    @GetMapping("/drafts")
    public ResponseEntity<List<Event>> getDraftEvents() {
        return ResponseEntity.ok(eventService.getDraftEvents());
    }

//    @GetMapping("/filter")
//    public List<Event> filterEvents(@RequestParam Map<String, String> filters) {
//        List<Long> eventIds = eventSearchService.searchByFilters(filters);
//        return eventService.findEventsByIds(eventIds);
//    }
//
//    @GetMapping("/filter/date")
//    public List<Event> filterEventsByDate(
//            @RequestParam(required = false) LocalDateTime fromDate,
//            @RequestParam(required = false) LocalDateTime toDate) {
//        List<Long> eventIds = eventSearchService.searchByDateRange(fromDate, toDate);
//        return eventService.findEventsByIds(eventIds);
//    }

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
}

