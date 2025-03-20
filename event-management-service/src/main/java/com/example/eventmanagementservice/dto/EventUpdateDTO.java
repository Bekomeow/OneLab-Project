package com.example.eventmanagementservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventUpdateDTO {
    private String title;
    private String description;
}
