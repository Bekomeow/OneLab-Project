package com.example.eventsearchservice.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.model.EventDocument;
import com.example.eventsearchservice.repository.EventSearchCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventSearchCustomRepositoryImpl implements EventSearchCustomRepository {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<EventDocument> findByDynamicFilters(EventStatus status, EventFormat format, String location) {
        List<Query> filters = new ArrayList<>();

        if (status != null) {
            filters.add(Query.of(q -> q.term(t -> t.field("status.keyword").value(status.name()))));
        }
        if (format != null) {
            filters.add(Query.of(q -> q.term(t -> t.field("eventFormat.keyword").value(format.name()))));
        }
        if (location != null && !location.isBlank()) {
            filters.add(Query.of(q -> q.term(t -> t.field("location.keyword").value(location))));
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b.must(filters));

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("events")
                .query(q -> q.bool(boolQuery))
        );

        try {
            SearchResponse<EventDocument> response = elasticsearchClient.search(searchRequest, EventDocument.class);
            return response.hits().hits().stream().map(hit -> hit.source()).toList();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка поиска в Elasticsearch", e);
        }
    }
}

