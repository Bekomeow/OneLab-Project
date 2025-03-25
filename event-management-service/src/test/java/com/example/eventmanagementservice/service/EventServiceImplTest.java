package com.example.eventmanagementservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.client.EventSearchClient;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.event.EventCreatedEvent;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.security.SecurityUtil;
import com.example.eventmanagementservice.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private KafkaTemplate<String, Long> longKafkaTemplate;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private EventSearchClient eventSearchClient;
    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDTO eventDTO;
    private EventUpdateDTO eventUpdateDTO;
    private Event draftEvent;
    private Event publishedEvent;

    @BeforeEach
    void setUp() {
        eventDTO = EventDTO.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().minusDays(1))
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("OrganizerName")
                .build();

        eventUpdateDTO = EventUpdateDTO.builder()
                .title("Title")
                .description("Description")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().minusDays(1))
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("test_user")
                .availableSeats(100)
                .build();

        draftEvent = Event.builder()
                .id(1L)
                .title("Draft Event")
                .description("Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().minusDays(1))
                .maxParticipants(100)
                .status(EventStatus.DRAFT)
                .organizerName("Organizer")
                .availableSeats(100)
                .build();

        publishedEvent = Event.builder()
                .id(1L)
                .title("Published Event")
                .description("Description")
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .maxParticipants(100)
                .status(EventStatus.PUBLISHED)
                .organizerName("Organizer")
                .availableSeats(100)
                .build();
    }

    @Test
    void createEvent_ShouldSaveAndIndexEvent() {
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test_user"));
        when(securityUtil.getAuthToken()).thenReturn("mock-token");
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            saved.setId(1L); // эмулируем сохранение
            return saved;
        });

        Event createdEvent = eventService.createEvent(eventDTO);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo(eventDTO.getTitle());
        assertThat(createdEvent.getDescription()).isEqualTo(eventDTO.getDescription());
        assertThat(createdEvent.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(createdEvent.getOrganizerName()).isEqualTo("test_user");

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(kafkaTemplate, times(1)).send(eq("event.created"), any(EventSearchDto.class));
        verify(eventPublisher, times(1)).publishEvent(any(EventCreatedEvent.class));
    }
    @Test
    void updateEvent_ShouldUpdateEventDetails() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(1L, eventUpdateDTO);

        assertThat(updatedEvent.getTitle()).isEqualTo(eventDTO.getTitle());
    }

    @Test
    void cancelEvent_ShouldChangeStatusAndSendKafkaMessages() {
        event.setStatus(EventStatus.COMPLETED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("test_user"));
        when(securityUtil.getEmailByUsername("test_user")).thenReturn("test@example.com");

        eventService.cancelEvent(1L, "No time");

        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);

        verify(eventRepository).save(event);
    }

    @Test
    void cancelEvent_ShouldThrowAccessDenied_WhenUserIsNotOwner() {
        event.setStatus(EventStatus.COMPLETED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityUtil.getCurrentUsername()).thenReturn(Optional.of("another_user"));

        assertThatThrownBy(() -> eventService.cancelEvent(1L, "fail"))
                .isInstanceOf(AccessDeniedException.class);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void eventExists_ShouldReturnTrueIfExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean exists = eventService.eventExists(1L);

        assertThat(exists).isTrue();
    }

    @Test
    void testGetDraftEvents() {
        when(eventRepository.findAllByStatusAndStartDateAfter(eq(EventStatus.DRAFT), any(LocalDateTime.class)))
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

    @Test
    void searchByKeyword_ShouldReturnEvents() {
        when(securityUtil.getAuthToken()).thenReturn("test-token");
        when(eventSearchClient.searchByKeyword("test", "Bearer test-token"))
                .thenReturn(List.of(1L, 2L));
        when(eventRepository.findAllByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(new Event(), new Event()));

        List<Event> result = eventService.searchByKeyword("test");

        assertThat(result).hasSize(2);
        verify(eventSearchClient).searchByKeyword("test", "Bearer test-token");
    }

    @Test
    void filterByStatusFormatLocation_ShouldReturnEvents() {
        when(securityUtil.getAuthToken()).thenReturn("test-token");
        when(eventSearchClient.filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty", "Bearer test-token"))
                .thenReturn(List.of(1L));
        when(eventRepository.findAllByIdIn(List.of(1L)))
                .thenReturn(List.of(new Event()));

        List<Event> result = eventService.filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty");

        assertThat(result).hasSize(1);
        verify(eventSearchClient).filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty", "Bearer test-token");
    }

    @Test
    void findEventsInDateRange_ShouldReturnEvents() {
        when(securityUtil.getAuthToken()).thenReturn("test-token");
        when(eventSearchClient.findEventsInDateRange("2024-01-01", "2024-12-31", "Bearer test-token"))
                .thenReturn(List.of(1L));
        when(eventRepository.findAllByIdIn(List.of(1L)))
                .thenReturn(List.of(new Event()));

        List<Event> result = eventService.findEventsInDateRange("2024-01-01", "2024-12-31");

        assertThat(result).hasSize(1);
        verify(eventSearchClient).findEventsInDateRange("2024-01-01", "2024-12-31", "Bearer test-token");
    }

    @Test
    void findEventsWithAvailableSeats_ShouldReturnEvents() {
        when(securityUtil.getAuthToken()).thenReturn("test-token");
        when(eventSearchClient.findEventsWithAvailableSeats(5, "Bearer test-token"))
                .thenReturn(List.of(1L));
        when(eventRepository.findAllByIdIn(List.of(1L)))
                .thenReturn(List.of(new Event()));

        List<Event> result = eventService.findEventsWithAvailableSeats(5);

        assertThat(result).hasSize(1);
        verify(eventSearchClient).findEventsWithAvailableSeats(5, "Bearer test-token");
    }

    @Test
    void getUpcomingEvents_ShouldReturnEvents() {
        when(securityUtil.getAuthToken()).thenReturn("test-token");
        when(eventSearchClient.getUpcomingEvents("Bearer test-token"))
                .thenReturn(List.of(1L, 2L));
        when(eventRepository.findAllByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(new Event(), new Event()));

        List<Event> result = eventService.getUpcomingEvents();

        assertThat(result).hasSize(2);
        verify(eventSearchClient).getUpcomingEvents("Bearer test-token");
    }
}
