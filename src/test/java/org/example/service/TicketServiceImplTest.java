package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.entity.Event;
import org.example.entity.Ticket;
import org.example.entity.User;
import org.example.enums.TicketStatus;
import org.example.repository.TicketRepository;
import org.example.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User user;
    private Event event;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        event = new Event();
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUser(user);
        ticket.setEvent(event);
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setStatus(TicketStatus.ACTIVE);
    }

    @Test
    void shouldGenerateTicketSuccessfully() {
        when(ticketRepository.existsByUserAndEvent(user, event)).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket generatedTicket = ticketService.generateTicket(user, event);

        assertThat(generatedTicket).isNotNull();
        assertThat(generatedTicket.getUser()).isEqualTo(user);
        assertThat(generatedTicket.getEvent()).isEqualTo(event);
        assertThat(generatedTicket.getStatus()).isEqualTo(TicketStatus.ACTIVE);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldThrowExceptionWhenTicketAlreadyExists() {
        when(ticketRepository.existsByUserAndEvent(user, event)).thenReturn(true);

        assertThatThrownBy(() -> ticketService.generateTicket(user, event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Билет уже создан");
    }

    @Test
    void shouldMarkTicketAsUsed() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        ticketService.markTicketAsUsed(ticket.getId());

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.USED);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void shouldThrowExceptionWhenMarkingNonexistentTicketAsUsed() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.markTicketAsUsed(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Билет не найден");
    }

    @Test
    void shouldCancelTicket() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        ticketService.cancelTicket(ticket.getId());

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CANCELLED);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void shouldThrowExceptionWhenCancelingNonexistentTicket() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.cancelTicket(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Билет не найден");
    }
}
