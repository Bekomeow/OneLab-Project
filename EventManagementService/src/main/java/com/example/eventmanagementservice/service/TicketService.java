package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;

public interface TicketService {
    Ticket generateTicket(Long userId, Event event);
    void markTicketAsUsed(Long ticketId);
    void cancelTicket(Long ticketId);
}
