package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventDTO {
    private Long id;
    private String name;
    private LocalDateTime date;
    private int maxParticipants;
    private List<TicketDTO> tickets;
    private EventStatus status;
}
