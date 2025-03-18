package com.example.eventmanagementservice.controller;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/events/performance")
@RequiredArgsConstructor
public class EventPerformanceController {

    private final EventService eventService;

    @GetMapping("/sequential")
    public ResponseEntity<String> measureSequentialProcessing() {
        Instant start = Instant.now();

        List<Event> events = eventService.getUpcomingEvents();
        List<String> processed = events.stream()
                .map(this::simulateProcessing).toList();

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();

        return ResponseEntity.ok("Sequential processing took: " + timeElapsed + " ms");
    }

    @GetMapping("/parallel")
    public ResponseEntity<String> measureParallelProcessing() {
        Instant start = Instant.now();

        List<Event> events = eventService.getUpcomingEvents();
        List<String> processed = events.parallelStream()
                .map(this::simulateProcessing).toList();

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();

        return ResponseEntity.ok("Parallel processing took: " + timeElapsed + " ms");
    }

    private String simulateProcessing(Event event) {
        try {
            Thread.sleep(50); // Симуляция нагрузки (50 мс на обработку)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Processed: " + event.getTitle();
    }
}
