package com.example.eventmanagementservice.repository;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {


    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event = :event")
    int countRegistrationsByEvent(@Param("event") Event event);

    List<Registration> findByUsername(String username);

    List<Registration> findByEvent(Event event);
}

