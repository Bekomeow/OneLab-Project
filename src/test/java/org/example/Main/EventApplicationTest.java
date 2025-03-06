package org.example.Main;

import org.example.EventApplication;
import org.example.dto.EventDTO;
import org.example.dto.UserDTO;
import org.example.entity.Event;
import org.example.entity.Registration;
import org.example.entity.User;
import org.example.enums.EventStatus;
import org.example.service.EventService;
import org.example.service.RegistrationService;
import org.example.service.TicketService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventApplicationTest {

    @Mock
    private EventService eventService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private TicketService ticketService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventApplication eventApplication;

    private User testUser;
    private Event testEvent;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setStatus(EventStatus.PUBLISHED);
        testEvent.setDate(LocalDateTime.now());
        testEvent.setMaxParticipants(100);
        testEvent.setOrganizer(testUser);

        testRegistration = new Registration();
        testRegistration.setId(1L);
        testRegistration.setUser(testUser);
        testRegistration.setEvent(testEvent);
    }

    @Test
    void testRegisterUser() {
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();
        when(userService.registerUser(userDTO)).thenReturn(testUser);

        User result = userService.registerUser(userDTO);

        assertThat(result).isEqualTo(testUser);
        verify(userService, times(1)).registerUser(userDTO);
    }

    @Test
    void testLogin() {
        when(userService.login("test@example.com", "password")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.login("test@example.com", "password");

        assertThat(result).isPresent().contains(testUser);
        verify(userService, times(1)).login("test@example.com", "password");
    }

    @Test
    void testCreateEvent() {
        EventDTO eventDTO = EventDTO.builder()
                .id(1L)
                .title("Test Event")
                .description("Description")
                .date(LocalDateTime.now())
                .maxParticipants(100)
                .organizerId(testUser.getId())
                .build();

        when(eventService.createEvent(eventDTO)).thenReturn(testEvent);

        Event result = eventService.createEvent(eventDTO);

        assertThat(result).isEqualTo(testEvent);
        verify(eventService, times(1)).createEvent(eventDTO);
    }

    @Test
    void testGetUpcomingEvents() {
        when(eventService.getUpcomingEvents()).thenReturn(List.of(testEvent));

        List<Event> events = eventService.getUpcomingEvents();

        assertThat(events).hasSize(1).contains(testEvent);
        verify(eventService, times(1)).getUpcomingEvents();
    }

    @Test
    void testRegisterUserForEvent() {
        when(registrationService.registerUserForEvent(1L, 1L)).thenReturn(testRegistration);

        Registration result = registrationService.registerUserForEvent(1L, 1L);

        assertThat(result).isEqualTo(testRegistration);
        verify(registrationService, times(1)).registerUserForEvent(1L, 1L);
    }
}
