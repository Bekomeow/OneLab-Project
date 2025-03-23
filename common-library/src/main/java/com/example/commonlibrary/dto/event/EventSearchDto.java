package com.example.commonlibrary.dto.event;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchDto {
    private Long eventId;
    private String title;
    private String description;
    private String location;
    private EventFormat eventFormat;
    private EventStatus status;
    private Integer maxParticipants;
    private Integer availableSeats;
    private Instant startDate;
    private Instant endDate;
}
