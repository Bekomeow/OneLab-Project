package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.enums.EventStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private int maxParticipants;
    private EventStatus status;
    private Long organizerId;
}
