package com.example.eventmanagementservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationDTO {
    private Long id;
    private String username;
    private Long eventId;
}
