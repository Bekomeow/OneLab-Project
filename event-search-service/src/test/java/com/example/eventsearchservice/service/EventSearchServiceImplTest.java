package com.example.eventsearchservice.service;

import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.model.EventDocument;
import com.example.eventsearchservice.repository.EventSearchRepository;
import com.example.eventsearchservice.service.impl.EventSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventSearchServiceImplTest {

    private EventSearchRepository eventSearchRepository;
    private EventSearchServiceImpl service;

    @BeforeEach
    void setUp() {
        eventSearchRepository = mock(EventSearchRepository.class);
        service = new EventSearchServiceImpl(eventSearchRepository);
    }

    @Test
    void saveEvent_shouldSaveCorrectly() {
        EventSearchDto dto = EventSearchDto.builder()
                .eventId(1L)
                .title("Test")
                .description("desc")
                .location("Almaty")
                .eventFormat(EventFormat.ONLINE)
                .maxParticipants(100)
                .availableSeats(90)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(3600))
                .build();

        service.saveEvent(dto);

        ArgumentCaptor<EventDocument> captor = ArgumentCaptor.forClass(EventDocument.class);
        verify(eventSearchRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(dto.getEventId());
        assertThat(captor.getValue().getTitle()).isEqualTo("Test");
    }

    @Test
    void updateEvent_shouldUpdateFields() {
        EventDocument existing = EventDocument.builder()
                .eventId(1L)
                .title("Old Title")
                .description("Old Desc")
                .status(EventStatus.DRAFT)
                .availableSeats(10)
                .build();

        when(eventSearchRepository.findByEventId(1L)).thenReturn(Optional.of(existing));

        EventSearchDto updateDto = EventSearchDto.builder()
                .eventId(1L)
                .title("New Title")
                .description("New Desc")
                .status(EventStatus.PUBLISHED)
                .availableSeats(50)
                .build();

        service.updateEvent(updateDto);

        verify(eventSearchRepository).save(existing);
        assertThat(existing.getTitle()).isEqualTo("New Title");
        assertThat(existing.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        assertThat(existing.getAvailableSeats()).isEqualTo(50);
    }

    @Test
    void deleteEvent_shouldCallRepository() {
        service.deleteEvent(5L);
        verify(eventSearchRepository).deleteByEventId(5L);
    }

    @Test
    void searchByKeyword_shouldReturnMatchingIds() {
        EventDocument doc = EventDocument.builder().eventId(1L).build();
        when(eventSearchRepository.findByTitleContainingOrDescriptionContaining("java", "java"))
                .thenReturn(List.of(doc));

        List<Long> result = service.searchByKeyword("java");

        assertThat(result).containsExactly(1L);
    }

    @Test
    void filterByStatusFormatLocation_shouldReturnFiltered() {
        EventDocument doc = EventDocument.builder().eventId(2L).build();
        when(eventSearchRepository.findByDynamicFilters(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty"))
                .thenReturn(List.of(doc));

        List<Long> result = service.filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty");

        assertThat(result).containsExactly(2L);
    }

    @Test
    void findEventsInDateRange_shouldReturnEventIds() {
        Instant from = Instant.now();
        Instant to = from.plusSeconds(3600);
        EventDocument doc = EventDocument.builder().eventId(3L).build();
        when(eventSearchRepository.findByStartDateBetween(from, to)).thenReturn(List.of(doc));

        List<Long> result = service.findEventsInDateRange(from, to);

        assertThat(result).containsExactly(3L);
    }

    @Test
    void findEventsWithAvailableSeats_shouldReturnEventIds() {
        EventDocument doc = EventDocument.builder().eventId(4L).build();
        when(eventSearchRepository.findByAvailableSeatsGreaterThan(20)).thenReturn(List.of(doc));

        List<Long> result = service.findEventsWithAvailableSeats(20);

        assertThat(result).containsExactly(4L);
    }

    @Test
    void getUpcomingEvents_shouldReturnSortedPublishedEvents() {
        EventDocument doc = EventDocument.builder().eventId(99L).build();
        when(eventSearchRepository.findByStartDateAfterAndStatusOrderByStartDateAsc(any(), eq(EventStatus.PUBLISHED)))
                .thenReturn(List.of(doc));

        List<Long> result = service.getUpcomingEvents();

        assertThat(result).containsExactly(99L);
    }
}
