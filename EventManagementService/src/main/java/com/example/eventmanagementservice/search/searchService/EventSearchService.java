package com.example.eventmanagementservice.search.searchService;

import com.example.eventmanagementservice.entity.Event;

import java.util.List;

public interface EventSearchService {
    List<Long> searchEventIds(String query);
    void indexEvent(Event event);
}
