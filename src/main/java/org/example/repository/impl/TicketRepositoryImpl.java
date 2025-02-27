package org.example.repository.impl;

import org.example.dto.TicketDTO;
import org.example.enums.TicketStatus;
import org.example.repository.TicketRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TicketRepositoryImpl implements TicketRepository {
    private final Map<UUID, TicketDTO> ticketStorage = new HashMap<>();

    @Override
    public TicketDTO createTicket(Long userId, Long eventId) {
        UUID ticketNumber = UUID.randomUUID();
        TicketDTO ticket = TicketDTO.builder()
                .id((long) (ticketStorage.size() + 1))
                .ticketNumber(ticketNumber)
                .userId(userId)
                .eventId(eventId)
                .status(TicketStatus.ACTIVE)
                .build();
        ticketStorage.put(ticketNumber, ticket);
        return ticket;
    }

    @Override
    public void cancelTicket(UUID ticketNumber) {
        ticketStorage.computeIfPresent(ticketNumber, (key, ticket) -> {
            ticket.setStatus(TicketStatus.CANCELLED);
            return ticket;
        });
    }

    @Override
    public Optional<TicketDTO> getTicketByNumber(UUID ticketNumber) {
        return Optional.ofNullable(ticketStorage.get(ticketNumber));
    }

    @Override
    public Optional<TicketDTO> findById(Long id) {
        return ticketStorage.values().stream()
                .filter(ticket -> Objects.equals(ticket.getId(), id)).findFirst();
    }

    @Override
    public List<TicketDTO> getTicketsByUser(Long userId) {
        return ticketStorage.values().stream()
                .filter(ticket -> ticket.getUserId().equals(userId))
                .toList();
    }
}

