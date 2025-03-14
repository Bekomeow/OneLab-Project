package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.service.impl.EventServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setDate(LocalDateTime.now().plusDays(1));
        event.setMaxParticipants(100);
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizerName("Test");

        eventDTO = EventDTO.builder()
                .id(1L)
                .title("Updated Event")
                .description("Updated Description")
                .maxParticipants(30)
                .build();

    }

    @Test
    void shouldCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(eventDTO);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo(event.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldUpdateEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(eventDTO);

        assertThat(updatedEvent.getTitle()).isEqualTo(eventDTO.getTitle());
        assertThat(updatedEvent.getDescription()).isEqualTo(eventDTO.getDescription());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event not found with ID: 1");
    }

    @Test
    void shouldPublishEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.publishEvent(1L);

        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldThrowExceptionWhenPublishingAlreadyPublishedEvent() {
        event.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publishEvent(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Мероприятие уже опубликовано");
    }

    @Test
    void shouldCancelEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.cancelEvent(1L);

        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldReturnUpcomingEvents() {
        when(eventRepository.findAllByStatusAndDateAfter(eq(EventStatus.PUBLISHED), any(LocalDateTime.class)))
                .thenReturn(List.of(event));

        List<Event> events = eventService.getUpcomingEvents();

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void shouldCheckIfEventExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean exists = eventService.eventExists(1L);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindEventsByIds() {
        when(eventRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(event));

        List<Event> events = eventService.findEventsByIds(List.of(1L));

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getId()).isEqualTo(1L);
    }
}
