package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import com.example.eventmanagementservice.enums.TicketStatus;
import com.example.eventmanagementservice.repository.TicketRepository;
import com.example.eventmanagementservice.service.impl.TicketServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Event event;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUsername("TestName");
        ticket.setEvent(event);
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setStatus(TicketStatus.ACTIVE);
    }

    @Test
    void generateTicket_ShouldCreateNewTicket() {
        when(ticketRepository.existsByUsernameAndEvent("TestName", event)).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.generateTicket("TestName", event);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("TestName");
        assertThat(result.getEvent()).isEqualTo(event);
        assertThat(result.getStatus()).isEqualTo(TicketStatus.ACTIVE);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void generateTicket_ShouldThrowException_WhenTicketAlreadyExists() {
        when(ticketRepository.existsByUsernameAndEvent("TestName", event)).thenReturn(true);

        assertThatThrownBy(() -> ticketService.generateTicket("TestName", event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Билет уже создан");
    }

    @Test
    void markTicketAsUsed_ShouldUpdateTicketStatus() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ticketService.markTicketAsUsed(1L);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.USED);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void markTicketAsUsed_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.markTicketAsUsed(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Билет не найден");
    }

    @Test
    void cancelTicket_ShouldUpdateTicketStatus() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ticketService.cancelTicket(1L);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CANCELLED);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void cancelTicket_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.cancelTicket(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Билет не найден");
    }
}
