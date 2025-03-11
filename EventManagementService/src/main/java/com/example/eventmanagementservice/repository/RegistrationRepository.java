package com.example.eventmanagementservice.repository;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserIdAndEvent(Long userId, Event event);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event = :event")
    int countRegistrationsByEvent(@Param("event") Event event);

    List<Registration> findByUserId(Long userId);

    List<Registration> findByEvent(Event event);
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
}

