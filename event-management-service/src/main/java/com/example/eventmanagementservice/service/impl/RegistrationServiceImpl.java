package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.EventRegistrationDto;
import com.example.eventmanagementservice.dto.RegistrationDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.repository.RegistrationRepository;
import com.example.eventmanagementservice.security.SecurityUtil;
import com.example.eventmanagementservice.service.RegistrationService;
import com.example.eventmanagementservice.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketService ticketService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SecurityUtil securityUtil;

    public Registration registerUserForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalStateException("Регистрация на это мероприятие закрыта");
        }

        if (event.getRegistrations().size() + 1 >= event.getMaxParticipants()) {
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
            eventRepository.save(event);
        }

        String currentUserName = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));;
        String userEmail = securityUtil.getEmailByUsername(currentUserName);

        Registration registration = new Registration();
        registration.setUsername(currentUserName);
        registration.setEvent(event);

        ticketService.generateTicket(currentUserName, event);

        EventRegistrationDto eventRegistration = EventRegistrationDto.builder()
                .email(userEmail)
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getStartDate())
                .maxParticipants(event.getMaxParticipants())
                .build();
        kafkaTemplate.send("event.registration.created", eventRegistration);

        return registrationRepository.save(registration);
    }

    public void unregisterUserFromEvent(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Регистрация не найдена"));

        Event event = registration.getEvent();

        if (!(event.getStatus().equals(EventStatus.PUBLISHED) || event.getStatus().equals(EventStatus.REGISTRATION_CLOSED))) {
            throw new IllegalStateException("Регистрация на это мероприятие закрыта, нельзя отменить регистрацию");
        }

        String currentUserName = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));

        if (event.getRegistrations().size() == event.getMaxParticipants()) {
            event.setStatus(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }

        ticketService.cancelTicket(registration.getEvent().getId(), currentUserName);

        registrationRepository.delete(registration);
    }

    public List<RegistrationDTO> getRegistrationsByUser() {
        String currentUserName = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));

        return registrationRepository.findByUsername(currentUserName)
                .stream().map(
                        registration -> RegistrationDTO.builder()
                        .id(registration.getId())
                        .username(registration.getUsername())
                        .eventTitle(registration.getEvent().getTitle())
                        .build()
                ).toList();
    }
}
