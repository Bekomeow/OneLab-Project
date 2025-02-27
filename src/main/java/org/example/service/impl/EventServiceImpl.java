package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.EventDTO;
import org.example.dto.TicketDTO;
import org.example.dto.UserDTO;
import org.example.enums.EventStatus;
import org.example.enums.TicketStatus;
import org.example.repository.EventRepository;
import org.example.repository.TicketRepository;
import org.example.repository.UserRepository;
import org.example.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public TicketDTO registerUserForEvent(Long userId, Long eventId) {
        Optional<EventDTO> eventOpt = eventRepository.findById(eventId);
        Optional<UserDTO> userOpt = userRepository.findById(userId);

        if (eventOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь или событие не найдены");
        }

        EventDTO event = eventOpt.get();
        UserDTO user = userOpt.get();
        if (event.getTickets().size() >= event.getMaxParticipants()) {
            throw new IllegalStateException("Лимит участников достигнут");
        }

        TicketDTO ticket = ticketRepository.createTicket(userId, eventId);

        event.getTickets().add(ticket);
        user.getTicketIds().add(ticket.getId());
        return ticket;
    }

    public void cancelRegistration(UUID ticketNumber) {
        Optional<TicketDTO> ticketOpt = ticketRepository.getTicketByNumber(ticketNumber);

        if (ticketOpt.isEmpty()) {
            throw new IllegalArgumentException("Билет не найден");
        }

        TicketDTO ticket = ticketOpt.get();

        Optional<EventDTO> eventOpt = eventRepository.findById(ticket.getEventId());
        Optional<UserDTO> userOpt = userRepository.findById(ticket.getUserId());

        EventDTO event = eventOpt.get();
        UserDTO user = userOpt.get();

        event.getTickets().remove(ticket);
        user.getTicketIds().remove(ticket.getId());

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.cancelTicket(ticketNumber);
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.getStatus() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        return eventRepository.save(eventDTO)
                .orElseGet(() -> {
                    System.out.println("Ошибка: не удалось создать событие.");
                    return null;
                });
    }

    public UserDTO registerUser(UserDTO userDTO) {
        return userRepository.save(userDTO)
                .orElseGet(() -> {
                    System.out.println("Ошибка: регистрация пользователя не удалась.");
                    return null;
                });
    }

    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll();
    }


    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id);
    }

}
