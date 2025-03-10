package com.example.eventmanagementservice.dto;

import com.example.eventmanagementservice.enums.EventStatus;
import lombok.Builder;
import lombok.Data;

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
