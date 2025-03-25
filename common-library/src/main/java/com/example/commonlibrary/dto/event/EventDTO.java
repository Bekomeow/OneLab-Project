package com.example.commonlibrary.dto.event;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxParticipants;
    private EventStatus status;
    private String organizerName;
    private String location;
    private EventFormat eventFormat;
}
