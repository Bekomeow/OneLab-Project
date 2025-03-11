package com.example.eventmanagementservice.search.searchService.impl;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.search.document.EventDocument;
import com.example.eventmanagementservice.search.searchRepository.EventSearchRepository;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {

    private final EventSearchRepository eventSearchRepository;

    public List<Long> searchEventIds(String query) {
        List<EventDocument> searchResults =
                eventSearchRepository.findByTitleContainingOrDescriptionContaining(query, query);

        return searchResults.stream()
                .map(EventDocument::getId)
                .collect(Collectors.toList());
    }


    public void indexEvent(Event event) {
        EventDocument document = EventDocument.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .build();
        eventSearchRepository.save(document);
    }
}
