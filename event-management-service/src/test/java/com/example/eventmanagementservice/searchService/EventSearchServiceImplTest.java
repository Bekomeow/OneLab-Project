//package com.example.eventmanagementservice.searchService;
//
//import com.example.eventmanagementservice.entity.Event;
//import com.example.eventmanagementservice.search.document.EventDocument;
//import com.example.eventmanagementservice.search.searchRepository.EventSearchRepository;
//import com.example.eventmanagementservice.search.searchService.impl.EventSearchServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.core.query.Query;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class EventSearchServiceImplTest {
//
//    @Mock
//    private EventSearchRepository eventSearchRepository;
//
//    @Mock
//    private ElasticsearchOperations elasticsearchOperations;
//
//    @InjectMocks
//    private EventSearchServiceImpl eventSearchService;
//
//    private Event event;
//    private EventDocument eventDocument;
//
//    @BeforeEach
//    void setUp() {
//        event = new Event();
//        event.setId(1L);
//        event.setTitle("Spring Boot Workshop");
//        event.setDescription("An advanced workshop on Spring Boot.");
//        event.setDate(LocalDateTime.now());
//
//        eventDocument = EventDocument.builder()
//                .eventId(event.getId())
//                .title(event.getTitle())
//                .description(event.getDescription())
//                .date(event.getDate().atZone(ZoneId.systemDefault()).toInstant())
//                .build();
//    }
//
//    @Test
//    void searchEventIds_ShouldReturnEventIds() {
//        when(eventSearchRepository.findByTitleContainingOrDescriptionContaining(anyString(), anyString()))
//                .thenReturn(List.of(eventDocument));
//
//        List<Long> result = eventSearchService.searchEventIds("Spring Boot");
//
//        assertThat(result).containsExactly(event.getId());
//        verify(eventSearchRepository, times(1)).findByTitleContainingOrDescriptionContaining("Spring Boot", "Spring Boot");
//    }
//
//    @Test
//    void indexEvent_ShouldSaveEventDocument() {
//        eventSearchService.indexEvent(event);
//        verify(eventSearchRepository, times(1)).save(any(EventDocument.class));
//    }
//
//    @Test
//    void searchByFilters_ShouldReturnEventIds() {
//        when(elasticsearchOperations.search(any(Query.class), eq(EventDocument.class)))
//                .thenReturn(mock(SearchHits.class));
//
//        List<Long> result = eventSearchService.searchByFilters(Map.of("title", "Spring Boot"));
//
//        assertThat(result).isEmpty();
//        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(EventDocument.class));
//    }
//
//    @Test
//    void searchByDateRange_ShouldThrowException_WhenBothDatesNull() {
//        assertThrows(IllegalArgumentException.class, () -> eventSearchService.searchByDateRange(null, null));
//    }
//
//    @Test
//    void searchByDateRange_ShouldUseCurrentDate_WhenFromDateIsNull() {
//        when(elasticsearchOperations.search(any(Query.class), eq(EventDocument.class)))
//                .thenReturn(mock(SearchHits.class));
//
//        List<Long> result = eventSearchService.searchByDateRange(null, LocalDateTime.now().plusDays(10));
//
//        assertThat(result).isEmpty();
//        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(EventDocument.class));
//    }
//}
//
