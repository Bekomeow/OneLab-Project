package org.example.repository;

import org.example.dto.EventDTO;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<EventDTO> findById(Long id);
    List<EventDTO> findAll();
    Optional<EventDTO> save(EventDTO event);
}
