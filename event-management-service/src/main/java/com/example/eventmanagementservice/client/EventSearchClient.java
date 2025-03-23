package com.example.eventmanagementservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;

import java.time.Instant;
import java.util.List;

@FeignClient(name = "event-search-service")
public interface EventSearchClient {

    @GetMapping("/api/events/search/keyword")
    List<Long> searchByKeyword(@RequestParam String keyword, @RequestHeader("Authorization") String token);

    @GetMapping("/api/events/search/filter")
    List<Long> filterByStatusFormatLocation(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventFormat format,
            @RequestParam(required = false) String location,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/events/search/date-range")
    List<Long> findEventsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/events/search/available-seats")
    List<Long> findEventsWithAvailableSeats(@RequestParam int minSeats, @RequestHeader("Authorization") String token);

    @GetMapping("/api/events/search/upcoming")
    List<Long> getUpcomingEvents(@RequestHeader("Authorization") String token);

    @GetMapping("/api/events/search/aggregations/dates")
    Object getEventsPerDateAggregation(@RequestHeader("Authorization") String token);

    @GetMapping("/api/events/search/popular")
    List<Long> getMostPopularEvents(@RequestHeader("Authorization") String token);
}
