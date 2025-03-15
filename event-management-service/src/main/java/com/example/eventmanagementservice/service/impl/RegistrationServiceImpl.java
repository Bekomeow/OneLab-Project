package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.client.AuthServiceClient;
import com.example.eventmanagementservice.dto.EventRegistration;
import com.example.eventmanagementservice.dto.RegistrationDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.repository.RegistrationRepository;
import com.example.eventmanagementservice.security.JwtUtil;
import com.example.eventmanagementservice.service.RegistrationService;
import com.example.eventmanagementservice.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketService ticketService;
    private final AuthServiceClient authServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtUtil jwtUtil;

    public String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return null;
    }

    public Registration registerUserForEvent(Long eventId) {
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

        String token = jwtUtil.getCurrentToken();
        if (token == null) {
            throw new RuntimeException("JWT Token not found");
        }

        String currentUserName = getCurrentUsername();
        String userEmail = authServiceClient.getEmailByUsername(currentUserName, "Bearer " + token);

        Registration registration = new Registration();
        registration.setUsername(currentUserName);
        registration.setEvent(event);

        ticketService.generateTicket(currentUserName, event);

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

    public List<RegistrationDTO> getRegistrationsByUser() {
        return registrationRepository.findByUsername(getCurrentUsername())
                .stream().map(
                        registration -> RegistrationDTO.builder()
                        .id(registration.getId())
                        .username(registration.getUsername())
                        .eventId(registration.getEvent().getId())
                        .build()
                ).toList();
    }
}
