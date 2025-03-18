package com.example.eventmanagementservice.repository;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    @Query("""
        SELECT e FROM Event e 
        WHERE e.startDate > CURRENT_TIMESTAMP 
          AND e.status NOT IN :excludedStatuses
        ORDER BY e.startDate ASC
    """)
    List<Event> findUpcomingEvents(@Param("excludedStatuses") List<EventStatus> excludedStatuses);


    List<Event> findAllByStatusAndStartDateAfter(EventStatus status, LocalDateTime dateTime);

    boolean existsById(Long id);

    List<Event> findAllByIdIn(List<Long> ids);

    @Query("""
        SELECT e FROM Event e 
        WHERE e.status IN :statuses 
          AND e.startDate <= :now
    """)
    List<Event> findAllByStatusInAndStartDateBefore(
            @Param("statuses") List<EventStatus> statuses,
            @Param("now") LocalDateTime now
    );

    List<Event> findByStatusAndEndDateBefore(EventStatus status, LocalDateTime endDate);

}
