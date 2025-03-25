package com.example.eventmanagementservice.service;

import com.example.commonlibrary.dto.event.EventRegisterResponse;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.repository.RegistrationRepository;
import com.example.eventmanagementservice.security.SecurityUtil;
import com.example.eventmanagementservice.service.impl.RegistrationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

    @Mock private RegistrationRepository registrationRepository;
    @Mock private EventRepository eventRepository;
    @Mock private TicketService ticketService;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks private RegistrationServiceImpl registrationService;

    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Desc")
                .startDate(LocalDateTime.now())
                .maxParticipants(10)
                .availableSeats(5)
                .status(EventStatus.PUBLISHED)
                .build();

        registration = new Registration();
        registration.setId(1L);
        registration.setUsername("user1");
        registration.setEvent(event);
    }

    @Test
    void registerUserForEvent_ShouldRegister() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("user1"));
        when(securityUtil.getEmailByUsername("user1")).thenReturn("user1@mail.com");
        when(registrationRepository.save(any())).thenReturn(registration);

        Registration result = registrationService.registerUserForEvent(1L);

        assertNotNull(result);
        verify(ticketService).generateTicket("user1", event);
        verify(kafkaTemplate, times(2)).send(anyString(), any());
    }

    @Test
    void unregisterUserFromEvent_ShouldRemoveRegistration() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("user1"));

        registrationService.unregisterUserFromEvent(1L);

        verify(ticketService).cancelTicket(1L, "user1");
        verify(registrationRepository).delete(registration);
        verify(kafkaTemplate).send(anyString(), any());
    }

    @Test
    void getRegistrationsByUser_ShouldReturnList() {
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("user1"));
        when(registrationRepository.findAllByUsername("user1")).thenReturn(List.of(registration));

        List<EventRegisterResponse> responses = registrationService.getRegistrationsByUser();

        assertEquals(1, responses.size());
        assertEquals("user1", responses.get(0).getUsername());
        assertEquals("Test Event", responses.get(0).getEventTitle());
    }

    @Test
    void deleteAllRegistrationsByUser_ShouldDelete() {
        event.setRegistrations(new ArrayList<>(List.of(registration)));
        registration.setEvent(event);
        when(registrationRepository.findAllByUsername("user1")).thenReturn(List.of(registration));

        registrationService.deleteAllRegistrationsByUser("user1");

        verify(ticketService).cancelTicket(1L, "user1");
        verify(eventRepository).save(any());
        verify(kafkaTemplate).send(anyString(), any());
    }

    @Test
    void deleteAllRegistrationsByUser_ShouldThrowIfEmpty() {
        when(registrationRepository.findAllByUsername("user1")).thenReturn(List.of());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                registrationService.deleteAllRegistrationsByUser("user1"));

        assertTrue(ex.getMessage().contains("не найдены"));
    }
}
