package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.entity.Event;
import org.example.entity.Registration;
import org.example.entity.User;
import org.example.enums.EventStatus;
import org.example.repository.EventRepository;
import org.example.repository.RegistrationRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.RegistrationServiceImpl;
import org.example.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketServiceImpl ticketService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User user;
    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Test User");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStatus(EventStatus.PUBLISHED);
        event.setMaxParticipants(100);

        registration = new Registration();
        registration.setId(1L);
        registration.setUser(user);
        registration.setEvent(event);
    }

    @Test
    void shouldRegisterUserForEventSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationService.registerUserForEvent(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getEvent()).isEqualTo(event);
        verify(ticketService).generateTicket(user, event);
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.registerUserForEvent(1L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.registerUserForEvent(1L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Мероприятие не найдено");
    }

    @Test
    void shouldThrowExceptionWhenEventNotPublished() {
        event.setStatus(EventStatus.DRAFT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> registrationService.registerUserForEvent(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Регистрация на это мероприятие закрыта");
    }

    @Test
    void shouldThrowExceptionWhenEventIsFull() {
        List<Registration> registrations = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            registrations.add(new Registration());
        }
        event.setRegistrations(registrations);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> registrationService.registerUserForEvent(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Лимит участников достигнут");

        assertThat(event.getStatus()).isEqualTo(EventStatus.REGISTRATION_CLOSED);
        verify(eventRepository).save(event);
    }

    @Test
    void shouldUnregisterUserFromEventSuccessfully() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        registrationService.unregisterUserFromEvent(1L);

        verify(registrationRepository).delete(registration);
    }

    @Test
    void shouldThrowExceptionWhenUnregisteringNonExistentRegistration() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.unregisterUserFromEvent(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Регистрация не найдена");
    }
}
