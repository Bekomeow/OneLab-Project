package com.example.eventsearchservice.repository;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.model.EventDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String>, EventSearchCustomRepository {

    List<EventDocument> findByTitleContainingOrDescriptionContaining(String title, String description);

    List<EventDocument> findByStartDateBetween(Instant from, Instant to);

    List<EventDocument> findByAvailableSeatsGreaterThan(int minSeats);

    List<EventDocument> findByStartDateAfterAndStatusOrderByStartDateAsc(Instant startDate, EventStatus status);

    @Query("""
        {
          "size": 0,
          "aggs": {
            "events_per_date": {
              "date_histogram": {
                "field": "startDate",
                "calendar_interval": "day"
              }
            }
          }
        }
    """)
    List<Object> countEventsPerDate();

    @Query("""
        {
          "size": 10,
          "sort": [
            { "maxParticipants": "desc" }
          ]
        }
    """)
    List<EventDocument> findMostPopularEvents();

    Optional<EventDocument> findByEventId(Long eventId);

    void deleteByEventId(Long eventId);
}
