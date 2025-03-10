package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import com.example.eventmanagementservice.entity.User;

public interface TicketService {
    Ticket generateTicket(User user, Event event);
    void markTicketAsUsed(Long ticketId);
    void cancelTicket(Long ticketId);
}
