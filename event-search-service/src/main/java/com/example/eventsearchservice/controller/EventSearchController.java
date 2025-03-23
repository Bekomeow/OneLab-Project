package com.example.eventsearchservice.controller;

import com.example.eventsearchservice.service.EventSearchService;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/events/search")
@RequiredArgsConstructor
public class EventSearchController {

    private final EventSearchService eventSearchService;

    @GetMapping("/keyword")
    public List<Long> searchByKeyword(@RequestParam String keyword) {
        return eventSearchService.searchByKeyword(keyword);
    }

    @GetMapping("/filter")
    public List<Long> filterByStatusFormatLocation(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventFormat format,
            @RequestParam(required = false) String location) {
        return eventSearchService.filterByStatusFormatLocation(status, format, location);
    }

    @GetMapping("/date-range")
    public List<Long> findEventsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return eventSearchService.findEventsInDateRange(from, to);
    }

    @GetMapping("/available-seats")
    public List<Long> findEventsWithAvailableSeats(@RequestParam int minSeats) {
        return eventSearchService.findEventsWithAvailableSeats(minSeats);
    }

    @GetMapping("/upcoming")
    public List<Long> getUpcomingEvents() {
        return eventSearchService.getUpcomingEvents();
    }

    @GetMapping("/aggregations/dates")
    public Object getEventsPerDateAggregation() {
        return eventSearchService.getEventsPerDateAggregation();
    }

    @GetMapping("/popular")
    public List<Long> getMostPopularEvents() {
        return eventSearchService.getMostPopularEvents();
    }
}
