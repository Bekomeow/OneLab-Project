package com.example.eventmanagementservice.repository;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketCode(String ticketCode);

    List<Ticket> findByEvent(Event event);

    boolean existsByUsernameAndEvent(String username, Event event);
}
