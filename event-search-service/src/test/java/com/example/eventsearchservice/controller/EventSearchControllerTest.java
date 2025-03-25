package com.example.eventsearchservice.controller;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.service.EventSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventSearchService eventSearchService;

    @Test
    void searchByKeyword_ShouldReturnIds() throws Exception {
        when(eventSearchService.searchByKeyword("spring")).thenReturn(List.of(1L, 2L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search/keyword")
                        .param("keyword", "spring"))
                .andExpect(status().isOk());

        verify(eventSearchService).searchByKeyword("spring");
    }

    @Test
    void filterByStatusFormatLocation_ShouldReturnIds() throws Exception {
        when(eventSearchService.filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty"))
                .thenReturn(List.of(1L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search/filter")
                        .param("status", "PUBLISHED")
                        .param("format", "ONLINE")
                        .param("location", "Almaty"))
                .andExpect(status().isOk());

        verify(eventSearchService).filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty");
    }

    @Test
    void findEventsInDateRange_ShouldReturnIds() throws Exception {
        Instant from = Instant.parse("2025-03-01T00:00:00Z");
        Instant to = Instant.parse("2025-03-31T00:00:00Z");

        when(eventSearchService.findEventsInDateRange(from, to)).thenReturn(List.of(1L, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search/date-range")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk());

        verify(eventSearchService).findEventsInDateRange(from, to);
    }

    @Test
    void findEventsWithAvailableSeats_ShouldReturnIds() throws Exception {
        when(eventSearchService.findEventsWithAvailableSeats(10)).thenReturn(List.of(4L, 5L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search/available-seats")
                        .param("minSeats", "10"))
                .andExpect(status().isOk());

        verify(eventSearchService).findEventsWithAvailableSeats(10);
    }

    @Test
    void getUpcomingEvents_ShouldReturnIds() throws Exception {
        when(eventSearchService.getUpcomingEvents()).thenReturn(List.of(6L, 7L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search/upcoming"))
                .andExpect(status().isOk());

        verify(eventSearchService).getUpcomingEvents();
    }
}
