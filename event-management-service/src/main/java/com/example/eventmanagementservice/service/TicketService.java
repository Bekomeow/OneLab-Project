package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;

public interface TicketService {
    Ticket generateTicket(String username, Event event);
    void markTicketAsUsed(Long eventId, String username);
    void cancelTicket(Long eventId, String username);
}
