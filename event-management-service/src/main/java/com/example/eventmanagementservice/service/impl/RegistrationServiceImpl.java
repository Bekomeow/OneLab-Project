package com.example.eventmanagementservice.service.impl;

import com.example.commonlibrary.dto.event.EventRegisterResponse;
import com.example.commonlibrary.dto.event.EventRegistrationDto;
import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
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
    private final KafkaTemplate<String, Object> jsonKafkaTemplate;
    private final SecurityUtil securityUtil;

    public Registration registerUserForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalStateException("Регистрация на это мероприятие закрыта");
        }

        if (event.getAvailableSeats() <= 1) {
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
        } else {
            event.setAvailableSeats(event.getAvailableSeats() - 1);
        }

        eventRepository.save(event);

        EventSearchDto eventSearchDto = EventSearchDto.builder()
                .eventId(event.getId())
                .status(event.getStatus())
                .availableSeats(event.getAvailableSeats())
                .build();

        jsonKafkaTemplate.send("event.updated", eventSearchDto);

        String currentUserName = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));;
        String userEmail = securityUtil.getEmailByUsername(currentUserName);

        Registration registration = new Registration();
        registration.setUsername(currentUserName);
        registration.setEvent(event);

        ticketService.generateTicket(currentUserName, event);

        registration = registrationRepository.save(registration);

        EventRegistrationDto eventRegistration = EventRegistrationDto.builder()
                .email(userEmail)
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getStartDate())
                .maxParticipants(event.getMaxParticipants())
                .build();

        jsonKafkaTemplate.send("event.registration.created", eventRegistration);

        return registration;
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

        event.setAvailableSeats(event.getAvailableSeats() + 1);
        eventRepository.save(event);

        ticketService.cancelTicket(registration.getEvent().getId(), currentUserName);

        registrationRepository.delete(registration);

        EventSearchDto eventSearchDto = EventSearchDto.builder()
                .eventId(event.getId())
                .availableSeats(event.getAvailableSeats())
                .build();

        jsonKafkaTemplate.send("event.updated", eventSearchDto);
    }

    public List<EventRegisterResponse> getRegistrationsByUser() {
        String currentUserName = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));

        return registrationRepository.findAllByUsername(currentUserName)
                .stream().map(
                        registration -> EventRegisterResponse.builder()
                        .id(registration.getId())
                        .username(registration.getUsername())
                        .eventTitle(registration.getEvent().getTitle())
                        .build()
                ).toList();
    }

    public void deleteAllRegistrationsByUser(String username) {
        List<Registration> registrations = registrationRepository.findAllByUsername(username)
                .stream().filter(registration ->
                        (registration.getEvent().getStatus().equals(EventStatus.PUBLISHED)
                                || registration.getEvent().getStatus().equals(EventStatus.REGISTRATION_CLOSED))).toList();

        if (registrations.isEmpty()) {
            throw new EntityNotFoundException("Регистрации для пользователя " + username + " не найдены");
        }

        for (Registration registration : registrations) {
            Event event = registration.getEvent();

            ticketService.cancelTicket(event.getId(), username);
            event.getRegistrations().remove(registration);
            event.setAvailableSeats(event.getAvailableSeats() + 1);
            eventRepository.save(event);

            EventSearchDto eventSearchDto = EventSearchDto.builder()
                    .eventId(event.getId())
                    .availableSeats(event.getAvailableSeats())
                    .build();

            jsonKafkaTemplate.send("event.updated", eventSearchDto);
        }
    }
}
