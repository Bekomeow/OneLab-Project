package org.example.repository;

import org.example.dto.TicketDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository {
    TicketDTO createTicket(Long userId, Long eventId);
    void cancelTicket(UUID ticketNumber);
    Optional<TicketDTO> getTicketByNumber(UUID ticketNumber);
    Optional<TicketDTO> findById(Long id);
    List<TicketDTO> getTicketsByUser(Long userId);
    List<TicketDTO> getTicketsByEvent(Long eventId);
    int deleteTicketsByEventAndUser(Long eventId, Long userId);
}
