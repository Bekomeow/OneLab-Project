package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.EventDTO;
import org.example.entity.Event;
import org.example.entity.User;
import org.example.enums.EventStatus;
import org.example.repository.EventRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventDTO eventDTO;
    private Event event;
    private User organizer;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setUsername("Organizer");

        eventDTO = EventDTO.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .date(LocalDateTime.now().plusDays(7))
                .maxParticipants(100)
                .organizerId(1L)
                .build();

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setDate(eventDTO.getDate());
        event.setMaxParticipants(100);
        event.setStatus(EventStatus.PUBLISHED);
        event.setOrganizer(organizer);
    }

    @Test
    void shouldCreateEventSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(eventDTO);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo("Test Event");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenOrganizerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(eventDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Организатор не найден");
    }

    @Test
    void shouldUpdateEventSuccessfully() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDTO updateDTO = EventDTO.builder()
                .id(1L)
                .title("Updated Event")
                .description("Updated Description")
                .maxParticipants(30)
                .build();

        Event updatedEvent = eventService.updateEvent(updateDTO);

        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getTitle()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getDescription()).isEqualTo("Updated Description");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event not found with ID: 1");
    }

    @Test
    void shouldPublishDraftEventSuccessfully() {
        event.setStatus(EventStatus.DRAFT);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.publishEvent(1L);

        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(eventRepository).save(event);
    }

    @Test
    void shouldThrowExceptionWhenPublishingAlreadyPublishedEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publishEvent(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Мероприятие уже опубликовано");
    }

    @Test
    void shouldCancelEventSuccessfully() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.cancelEvent(1L);

        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);
        verify(eventRepository).save(event);
    }

    @Test
    void shouldGetUpcomingEvents() {
        when(eventRepository.findAllByStatusAndDateAfter(eq(EventStatus.PUBLISHED), any(LocalDateTime.class)))
                .thenReturn(List.of(event));

        List<Event> events = eventService.getUpcomingEvents();

        assertThat(events).isNotEmpty().hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Test Event");
    }
}
