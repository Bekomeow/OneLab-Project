package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.entity.User;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.repository.RegistrationRepository;
import com.example.eventmanagementservice.repository.UserRepository;
import com.example.eventmanagementservice.service.RegistrationService;
import com.example.eventmanagementservice.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;

    public Registration registerUserForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalStateException("Регистрация на это мероприятие закрыта");
        }

        if (event.getRegistrations().size() >= event.getMaxParticipants()) {
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
            eventRepository.save(event);
            throw new IllegalStateException("Лимит участников достигнут");
        }

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);

        ticketService.generateTicket(user, event);

        return registrationRepository.save(registration);
    }

    public void unregisterUserFromEvent(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Регистрация не найдена"));

        registrationRepository.delete(registration);
    }
}
