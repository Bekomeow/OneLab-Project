package com.example.eventsearchservice.model;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
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

    private String location;

    @Field(type = FieldType.Keyword)
    private EventFormat eventFormat;

    @Field(type = FieldType.Keyword)
    private EventStatus status;

    private Integer maxParticipants;

    private Integer availableSeats;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant startDate;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant endDate;
}
