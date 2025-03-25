package com.example.eventmanagementservice.service;

import com.example.commonlibrary.enums.event.TicketStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import com.example.eventmanagementservice.repository.TicketRepository;
import com.example.eventmanagementservice.service.impl.TicketServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private final String username = "testUser";
    private final Event event = new Event();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTicket_ShouldReturnNewTicket() {
        when(ticketRepository.existsByUsernameAndEvent(username, event)).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        Ticket ticket = ticketService.generateTicket(username, event);

        assertNotNull(ticket.getTicketCode());
        assertEquals(username, ticket.getUsername());
        assertEquals(event, ticket.getEvent());
        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void generateTicket_WhenTicketAlreadyExists_ShouldThrowException() {
        when(ticketRepository.existsByUsernameAndEvent(username, event)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> ticketService.generateTicket(username, event));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void markTicketAsUsed_ShouldUpdateStatusToUsed() {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.ACTIVE);

        when(ticketRepository.findByEventIdAndUsername(anyLong(), eq(username))).thenReturn(Optional.of(ticket));

        ticketService.markTicketAsUsed(1L, username);

        assertEquals(TicketStatus.USED, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void markTicketAsUsed_WhenNotFound_ShouldThrowException() {
        when(ticketRepository.findByEventIdAndUsername(anyLong(), eq(username))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.markTicketAsUsed(1L, username));
    }

    @Test
    void cancelTicket_ShouldDeleteTicket() {
        Ticket ticket = new Ticket();
        when(ticketRepository.findByEventIdAndUsername(anyLong(), eq(username))).thenReturn(Optional.of(ticket));

        ticketService.cancelTicket(1L, username);

        verify(ticketRepository).delete(ticket);
    }

    @Test
    void cancelTicket_WhenNotFound_ShouldThrowException() {
        when(ticketRepository.findByEventIdAndUsername(anyLong(), eq(username))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.cancelTicket(1L, username));
    }
}
