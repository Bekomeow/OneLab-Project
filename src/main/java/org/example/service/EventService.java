package org.example.service;

import org.example.dto.EventDTO;
import org.example.dto.TicketDTO;
import org.example.dto.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    TicketDTO registerUserForEvent(Long userId, Long eventId);

    void cancelRegistration(UUID ticketNumber);

    List<EventDTO> getUpcomingEvents();
    EventDTO createEvent(EventDTO eventDTO);
    UserDTO registerUser(UserDTO userDTO);
    Optional<EventDTO> getEventById(Long id);
    List<EventDTO> getAllEvents();
    Optional<UserDTO> getUserById(Long id);
}
