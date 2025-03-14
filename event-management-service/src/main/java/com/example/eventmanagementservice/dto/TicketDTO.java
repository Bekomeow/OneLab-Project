package com.example.eventmanagementservice.dto;

import com.example.eventmanagementservice.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDTO {
    private Long id;
    private String ticketCode;
    private TicketStatus status;
    private Long userId;
    private Long eventId;
}
