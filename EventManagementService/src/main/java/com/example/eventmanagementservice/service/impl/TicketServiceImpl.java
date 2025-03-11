package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import com.example.eventmanagementservice.enums.TicketStatus;
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

    public Ticket generateTicket(Long userId, Event event) {
        if (ticketRepository.existsByUserIdAndEvent(userId, event)) {
            throw new IllegalStateException("Билет уже создан");
        }

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setEvent(event);
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setStatus(TicketStatus.ACTIVE);

        return ticketRepository.save(ticket);
    }

    public void markTicketAsUsed(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Билет не найден"));

        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);
    }

    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Билет не найден"));

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
    }
}
