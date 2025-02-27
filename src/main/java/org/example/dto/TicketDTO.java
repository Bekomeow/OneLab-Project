package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.enums.TicketStatus;

import java.util.UUID;

@Data
@Builder
public class TicketDTO {
    private Long id;
    private UUID ticketNumber;
    private Long userId;
    private Long eventId;
    private TicketStatus status;
}
