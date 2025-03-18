package com.example.eventmanagementservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.dto.EventStatusDto;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.security.SecurityUtil;
import com.example.eventmanagementservice.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventSearchService eventSearchService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDTO eventDTO;
    private Event draftEvent;
    private Event publishedEvent;

    @BeforeEach
    void setUp() {
        eventDTO = EventDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .date(LocalDateTime.now())
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("OrganizerName")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Description")
                .date(LocalDateTime.now())
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("test_user")
                .build();

        draftEvent = Event.builder()
                .id(1L)
                .title("Draft Event")
                .description("Description")
                .date(LocalDateTime.now().plusDays(1))
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("Organizer")
                .build();

        publishedEvent = Event.builder()
                .id(1L)
                .title("Published Event")
                .description("Description")
                .date(LocalDateTime.now().plusDays(2))
                .maxParticipants(100)
                .status(EventStatus.PUBLISHED)
                .organizerName("Organizer")
                .build();
    }

    @Test
    void createEvent_ShouldSaveAndIndexEvent() {
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test_user"));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(eventDTO);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo("Test Event");
        verify(eventSearchService, times(1)).indexEvent(any(Event.class));
    }

    @Test
    void updateEvent_ShouldUpdateEventDetails() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(eventDTO);

        assertThat(updatedEvent.getTitle()).isEqualTo(eventDTO.getTitle());
    }

    @Test
    void publishEvent_ShouldChangeStatusAndSendKafkaMessage() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityUtil.getEmailByUsername(anyString())).thenReturn("test@example.com");

        eventService.publishEvent(1L);

        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(kafkaTemplate, times(1)).send(eq("event.status.notification"), any(EventStatusDto.class));
    }

    @Test
    void cancelEvent_ShouldChangeStatusAndSendNotification() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test_user"));
        when(securityUtil.getEmailByUsername(anyString())).thenReturn("test@example.com");

        eventService.cancelEvent(1L, "Reason");

        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);
        verify(kafkaTemplate, times(1)).send(eq("event.status.notification"), any(EventStatusDto.class));
    }

    @Test
    void getUpcomingEvents_ShouldReturnPublishedEvents() {
        when(eventRepository.findAllByStatusAndDateAfter(eq(EventStatus.PUBLISHED), any(LocalDateTime.class)))
                .thenReturn(List.of(event));

        List<Event> events = eventService.getUpcomingEvents();

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Test Event");
    }

    @Test
    void eventExists_ShouldReturnTrueIfExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean exists = eventService.eventExists(1L);

        assertThat(exists).isTrue();
    }

    @Test
    void testGetDraftEvents() {
        when(eventRepository.findAllByStatusAndDateAfter(eq(EventStatus.DRAFT), any(LocalDateTime.class)))
                .thenReturn(List.of(draftEvent));

        List<Event> result = eventService.getDraftEvents();

        assertThat(result).hasSize(1).containsExactly(draftEvent);
    }

    @Test
    void testEventExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean exists = eventService.eventExists(1L);

        assertThat(exists).isTrue();
    }

    @Test
    void testFindEventsByIds() {
        when(eventRepository.findAllByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(draftEvent, publishedEvent));

        List<Event> result = eventService.findEventsByIds(List.of(1L, 2L));

        assertThat(result).hasSize(1).containsExactly(publishedEvent);
    }

    @Test
    void testGetEventWithMostParticipants() {
        when(eventRepository.findAll()).thenReturn(List.of(draftEvent, publishedEvent));

        Optional<Event> result = eventService.getEventWithMostParticipants();

        assertThat(result).isPresent().contains(publishedEvent);
    }

    @Test
    void testGroupEventsByStatus() {
        when(eventRepository.findAll()).thenReturn(List.of(draftEvent, publishedEvent));

        Map<EventStatus, List<Event>> result = eventService.groupEventsByStatus();

        assertThat(result).containsKeys(EventStatus.DRAFT, EventStatus.PUBLISHED);
        assertThat(result.get(EventStatus.DRAFT)).containsExactly(draftEvent);
        assertThat(result.get(EventStatus.PUBLISHED)).containsExactly(publishedEvent);
    }

    @Test
    void testPartitionEventsByDate() {
        when(eventRepository.findAll()).thenReturn(List.of(draftEvent, publishedEvent));

        Map<Boolean, List<Event>> result = eventService.partitionEventsByDate();

        assertThat(result.get(true)).containsExactlyInAnyOrder(draftEvent, publishedEvent);
        assertThat(result.get(false)).isEmpty();
    }
}
