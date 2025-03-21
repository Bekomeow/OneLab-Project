package com.example.eventmanagementservice.dto;

import com.example.eventmanagementservice.enums.EventStatus;
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
    private LocalDateTime date;
    private int maxParticipants;
    private EventStatus status;
    private String organizerName;
}
