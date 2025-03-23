package com.example.eventmanagementservice.search.document;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Builder
@Document(indexName = "events")
public class EventDocument {

    @Id
    private String id;

    private Long eventId;

    private String title;

    private String description;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant date;
}

