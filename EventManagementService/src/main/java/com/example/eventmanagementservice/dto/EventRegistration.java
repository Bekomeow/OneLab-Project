package com.example.eventmanagementservice.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRegistration {
    private String title;
    private String description;
    private LocalDateTime date;
    private int maxParticipants;
    private String email;
}
