package com.example.eventmanagementservice.service.impl;

import com.example.commonlibrary.enums.event.TicketStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import com.example.eventmanagementservice.repository.TicketRepository;
import com.example.eventmanagementservice.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public Ticket generateTicket(String username, Event event) {
        if (ticketRepository.existsByUsernameAndEvent(username, event)) {
            throw new IllegalStateException("Билет уже создан");
        }

        Ticket ticket = new Ticket();
        ticket.setUsername(username);
        ticket.setEvent(event);
        ticket.setTicketCode(UUID.randomUUID());
        ticket.setStatus(TicketStatus.ACTIVE);

        return ticketRepository.save(ticket);
    }

    public void markTicketAsUsed(Long eventId, String username) {
        Ticket ticket = ticketRepository.findByEventIdAndUsername(eventId, username)
                .orElseThrow(() -> new EntityNotFoundException("Билет не найден"));

        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);
    }

    public void cancelTicket(Long eventId, String username) {
        Ticket ticket = ticketRepository.findByEventIdAndUsername(eventId, username)
                .orElseThrow(() -> new EntityNotFoundException("Билет не найден"));

        ticketRepository.delete(ticket);
    }
}
