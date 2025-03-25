package com.example.eventsearchservice.listener;

import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.eventsearchservice.service.EventSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EventListenerTest {

    private EventSearchService eventSearchService;
    private EventListener eventListener;

    @BeforeEach
    void setUp() {
        eventSearchService = mock(EventSearchService.class);
        eventListener = new EventListener(eventSearchService);
    }

    @Test
    void handleEventCreated_shouldCallSaveEvent() {
        EventSearchDto eventDto = EventSearchDto.builder()
                .eventId(1L)
                .title("Created Event")
                .build();

        eventListener.handleEventCreated(eventDto);

        verify(eventSearchService, times(1)).saveEvent(eventDto);
    }

    @Test
    void handleEventUpdated_shouldCallUpdateEvent() {
        EventSearchDto eventDto = EventSearchDto.builder()
                .eventId(2L)
                .title("Updated Event")
                .build();

        eventListener.handleEventUpdated(eventDto);

        verify(eventSearchService, times(1)).updateEvent(eventDto);
    }

    @Test
    void handleEventDeleted_shouldCallDeleteEvent() {
        Long eventId = 3L;

        eventListener.handleEventDeleted(eventId);

        verify(eventSearchService, times(1)).deleteEvent(eventId);
    }
}
