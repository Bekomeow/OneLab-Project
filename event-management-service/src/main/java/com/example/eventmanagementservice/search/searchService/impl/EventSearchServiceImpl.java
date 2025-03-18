package com.example.eventmanagementservice.search.searchService.impl;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.search.document.EventDocument;
import com.example.eventmanagementservice.search.searchRepository.EventSearchRepository;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {

    private final EventSearchRepository eventSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<Long> searchEventIds(String query) {
        List<EventDocument> searchResults =
                eventSearchRepository.findByTitleContainingOrDescriptionContaining(query, query);

        return searchResults.stream()
                .map(EventDocument::getEventId)
                .collect(Collectors.toList());
    }


    public void indexEvent(Event event) {
        EventDocument document = EventDocument.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate().atZone(ZoneId.systemDefault()).toInstant())
                .build();
        eventSearchRepository.save(document);
    }

    public List<Long> searchByFilters(Map<String, String> filters) {
        Criteria criteria = new Criteria();

        filters.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {
                criteria.and(new Criteria(key).is(value));
            }
        });

        Query query = new CriteriaQuery(criteria);
        return executeSearch(query);
    }

    public List<Long> searchByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {

        if (fromDate == null && toDate == null) {
            throw new IllegalArgumentException("Необходимо задать хотя бы один из параметров: fromDate или toDate");
        }

        if (fromDate == null || fromDate.isBefore(LocalDateTime.now())) {
            fromDate = LocalDateTime.now();
        }

        Criteria criteria = new Criteria("date")
                .greaterThanEqual(fromDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        if (toDate != null) {
            criteria.lessThanEqual(toDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        Query query = new CriteriaQuery(criteria);
        return executeSearch(query);
    }

    private List<Long> executeSearch(Query query) {
        SearchHits<EventDocument> searchHits = elasticsearchOperations.search(query, EventDocument.class);
        return searchHits.stream()
                .map(hit -> hit.getContent().getEventId())
                .collect(Collectors.toList());
    }

}
