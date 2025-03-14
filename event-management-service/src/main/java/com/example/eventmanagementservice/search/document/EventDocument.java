package com.example.eventmanagementservice.search.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@Document(indexName = "events")
public class EventDocument {

    @Id
    private Long id;

    private String title;

    private String description;
}
