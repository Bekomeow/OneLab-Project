package com.example.eventmanagementservice.searchService;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.search.document.EventDocument;
import com.example.eventmanagementservice.search.searchRepository.EventSearchRepository;
import com.example.eventmanagementservice.search.searchService.impl.EventSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EventSearchServiceImplTest {

    @Mock
    private EventSearchRepository eventSearchRepository;

    @InjectMocks
    private EventSearchServiceImpl eventSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchEventIds_ShouldReturnListOfEventIds_WhenDocumentsFound() {
        EventDocument doc1 = EventDocument.builder()
                .id(1L)
                .title("Title1")
                .description("Description1")
                .build();

        EventDocument doc2 = EventDocument.builder()
                .id(2L)
                .title("Title2")
                .description("Description2")
                .build();

        when(eventSearchRepository.findByTitleContainingOrDescriptionContaining("query", "query"))
                .thenReturn(List.of(doc1, doc2));

        List<Long> result = eventSearchService.searchEventIds("query");

        assertThat(result).containsExactly(1L, 2L);
        verify(eventSearchRepository).findByTitleContainingOrDescriptionContaining("query", "query");
    }

    @Test
    void searchEventIds_ShouldReturnEmptyList_WhenNoDocumentsFound() {
        when(eventSearchRepository.findByTitleContainingOrDescriptionContaining("query", "query"))
                .thenReturn(List.of());

        List<Long> result = eventSearchService.searchEventIds("query");

        assertThat(result).isEmpty();
        verify(eventSearchRepository).findByTitleContainingOrDescriptionContaining("query", "query");
    }

    @Test
    void indexEvent_ShouldMapEventToDocumentAndSave() {
        Event event = Event.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .maxParticipants(10)
                .build();

        eventSearchService.indexEvent(event);

        ArgumentCaptor<EventDocument> captor = ArgumentCaptor.forClass(EventDocument.class);
        verify(eventSearchRepository).save(captor.capture());

        EventDocument savedDocument = captor.getValue();
        assertThat(savedDocument.getId()).isEqualTo(1L);
        assertThat(savedDocument.getTitle()).isEqualTo("Event Title");
        assertThat(savedDocument.getDescription()).isEqualTo("Event Description");
    }
}

