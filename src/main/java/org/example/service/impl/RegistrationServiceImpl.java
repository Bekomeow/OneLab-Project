package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.entity.Event;
import org.example.entity.Registration;
import org.example.entity.User;
import org.example.enums.EventStatus;
import org.example.repository.EventRepository;
import org.example.repository.RegistrationRepository;
import org.example.repository.UserRepository;
import org.example.service.RegistrationService;
import org.example.service.TicketService;
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
