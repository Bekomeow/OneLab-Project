package com.example.eventmanagementservice.controller;

import com.example.eventmanagementservice.dto.CancelEventRequest;
import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventSearchService eventSearchService;

    @Test
    void createEvent_ShouldReturnEvent() throws Exception {
        EventDTO eventDTO = new EventDTO();
        Event mockEvent = new Event();
        mockEvent.setId(1L);

        when(eventService.createEvent(any(EventDTO.class))).thenReturn(mockEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() throws Exception {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(1L);
        eventDTO.setTitle("Updated Event");

        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setTitle("Updated Event");

        when(eventService.updateEvent(any(EventDTO.class))).thenReturn(updatedEvent);

        mockMvc.perform(MockMvcRequestBuilders.put("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Event"));

        verify(eventService, times(1)).updateEvent(any(EventDTO.class));
    }

    @Test
    void publishEvent_ShouldReturnNoContent() throws Exception {
        Long eventId = 1L;
        doNothing().when(eventService).publishEvent(eventId);

        mockMvc.perform(MockMvcRequestBuilders.post("/events/{eventId}/publish", eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelEvent_ShouldReturnNoContent() throws Exception {
        Long eventId = 1L;
        CancelEventRequest request = new CancelEventRequest("Cancelled due to unforeseen circumstances");
        doNothing().when(eventService).cancelEvent(eq(eventId), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/events/{eventId}/cancel", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Cancelled due to unforeseen circumstances\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUpcomingEvents_ShouldReturnEventList() throws Exception {
        List<Event> events = List.of(new Event());
        when(eventService.getUpcomingEvents()).thenReturn(events);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getDraftEvents_ShouldReturnDraftEvents() throws Exception {
        Event draftEvent = new Event();
        draftEvent.setId(1L);
        draftEvent.setStatus(EventStatus.DRAFT);

        when(eventService.getDraftEvents()).thenReturn(List.of(draftEvent));

        mockMvc.perform(MockMvcRequestBuilders.get("/events/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));

        verify(eventService, times(1)).getDraftEvents();
    }

    @Test
    void filterEvents_ShouldReturnFilteredEvents() throws Exception {
        Map<String, String> filters = Collections.singletonMap("location", "New York");
        List<Long> eventIds = List.of(1L);
        Event event = new Event();
        event.setId(1L);

        when(eventSearchService.searchByFilters(filters)).thenReturn(eventIds);
        when(eventService.findEventsByIds(eventIds)).thenReturn(List.of(event));

        mockMvc.perform(MockMvcRequestBuilders.get("/events/filter")
                        .param("location", "New York"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(eventSearchService, times(1)).searchByFilters(filters);
        verify(eventService, times(1)).findEventsByIds(eventIds);
    }

    @Test
    void filterEventsByDate_ShouldReturnFilteredEvents() throws Exception {
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = fromDate.plusDays(5);
        List<Long> eventIds = List.of(1L);
        Event event = new Event();
        event.setId(1L);

        when(eventSearchService.searchByDateRange(fromDate, toDate)).thenReturn(eventIds);
        when(eventService.findEventsByIds(eventIds)).thenReturn(List.of(event));

        mockMvc.perform(MockMvcRequestBuilders.get("/events/filter/date")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(eventSearchService, times(1)).searchByDateRange(fromDate, toDate);
        verify(eventService, times(1)).findEventsByIds(eventIds);
    }

    @Test
    void getPartitionedEvents_ShouldReturnPartitionedEvents() throws Exception {
        Map<Boolean, List<Event>> partitionedEvents = Map.of(
                true, List.of(new Event()),
                false, List.of()
        );
        when(eventService.partitionEventsByDate()).thenReturn(partitionedEvents);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/stream/partitioned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.true").isArray());

        verify(eventService, times(1)).partitionEventsByDate();
    }

    @Test
    void getMostPopularEvent_ShouldReturnEvent() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.getEventWithMostParticipants()).thenReturn(Optional.of(event));

        mockMvc.perform(MockMvcRequestBuilders.get("/events/stream/most-popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getGroupedEvents_ShouldReturnGroupedEvents() throws Exception {
        Map<EventStatus, List<Event>> groupedEvents = Map.of(EventStatus.PUBLISHED, List.of(new Event()));
        when(eventService.groupEventsByStatus()).thenReturn(groupedEvents);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/stream/grouped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PUBLISHED").exists());
    }
}
