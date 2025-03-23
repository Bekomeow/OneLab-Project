package com.example.eventmanagementservice.search.searchRepository;

import com.example.eventmanagementservice.search.document.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, Long> {

    List<EventDocument> findByTitleContainingOrDescriptionContaining(String title, String description);
}
