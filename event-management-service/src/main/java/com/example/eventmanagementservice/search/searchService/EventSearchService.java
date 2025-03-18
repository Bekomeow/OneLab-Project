package com.example.eventmanagementservice.search.searchService;

import com.example.eventmanagementservice.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventSearchService {
    List<Long> searchEventIds(String query);
    void indexEvent(Event event);
    List<Long> searchByFilters(Map<String, String> filters);
    List<Long> searchByDateRange(LocalDateTime fromDate, LocalDateTime toDate);
}
