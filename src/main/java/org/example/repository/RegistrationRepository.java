package org.example.repository;

import org.example.entity.Event;
import org.example.entity.Registration;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserAndEvent(User user, Event event);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event = :event")
    int countRegistrationsByEvent(@Param("event") Event event);

    List<Registration> findByUser(User user);

    List<Registration> findByEvent(Event event);
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
}

