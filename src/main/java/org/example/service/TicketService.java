package org.example.service;

import org.example.entity.Event;
import org.example.entity.Ticket;
import org.example.entity.User;

public interface TicketService {
    Ticket generateTicket(User user, Event event);
    void markTicketAsUsed(Long ticketId);
    void cancelTicket(Long ticketId);
}
