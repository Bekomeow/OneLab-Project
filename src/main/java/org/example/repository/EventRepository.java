package org.example.repository;

import org.example.entity.Event;
import org.example.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.date > CURRENT_TIMESTAMP ORDER BY e.date ASC")
    List<Event> findUpcomingEvents();

    List<Event> findAllByStatusAndDateAfter(EventStatus status, LocalDateTime dateTime);


}
