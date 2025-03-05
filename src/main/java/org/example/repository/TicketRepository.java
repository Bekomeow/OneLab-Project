package org.example.repository;

import org.example.entity.Event;
import org.example.entity.Ticket;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketCode(String ticketCode);

    List<Ticket> findByUser(User user);

    List<Ticket> findByEvent(Event event);

    boolean existsByUserAndEvent(User user, Event event);
}
