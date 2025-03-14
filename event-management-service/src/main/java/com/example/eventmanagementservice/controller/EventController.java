package com.example.eventmanagementservice.controller;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PutMapping
    public ResponseEntity<Event> updateEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(eventDTO));
    }

    @PostMapping("/{eventId}/publish")
    public ResponseEntity<Void> publishEvent(@PathVariable Long eventId) {
        eventService.publishEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    //FOR MODERATOR AND EVENT OWNER
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long eventId) {
        eventService.cancelEvent(eventId);
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
}

