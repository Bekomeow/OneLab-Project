package com.example.eventmanagementservice.controller;

import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventPerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void measureSequentialProcessing_ShouldReturnTime() throws Exception {
        List<Event> mockEvents = List.of(Event.builder().id(1L).title("Event 1").maxParticipants(5).build(), Event.builder().id(1L).title("Event 2").maxParticipants(10).build());
        when(eventService.getUpcomingEvents()).thenReturn(mockEvents);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/performance/sequential")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sequential processing took:")));

        verify(eventService, times(1)).getUpcomingEvents();
    }

    @Test
    void measureParallelProcessing_ShouldReturnTime() throws Exception {
        List<Event> mockEvents = List.of(Event.builder().id(1L).title("Event 1").maxParticipants(5).build(), Event.builder().id(1L).title("Event 2").maxParticipants(10).build());
        when(eventService.getUpcomingEvents()).thenReturn(mockEvents);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/performance/parallel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parallel processing took:")));

        verify(eventService, times(1)).getUpcomingEvents();
    }
}
