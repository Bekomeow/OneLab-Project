package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.EventRegistration;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.repository.RegistrationRepository;
import com.example.eventmanagementservice.service.RegistrationService;
import com.example.eventmanagementservice.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketService ticketService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Registration registerUserForEvent(Long userId, String userEmail, Long eventId) {
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
        registration.setUserId(userId);
        registration.setEvent(event);

        ticketService.generateTicket(userId, event);

        EventRegistration eventRegistration = EventRegistration.builder()
                .email(userEmail)
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .maxParticipants(event.getMaxParticipants())
                .build();
        kafkaTemplate.send("event.registration.created", eventRegistration);

        return registrationRepository.save(registration);
    }

    public void unregisterUserFromEvent(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Регистрация не найдена"));

        registrationRepository.delete(registration);
    }
}
