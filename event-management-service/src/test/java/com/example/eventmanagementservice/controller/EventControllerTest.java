package com.example.eventmanagementservice.controller;

import com.example.commonlibrary.dto.event.CancelEventRequest;
import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private EventService eventService;

    @Test
    void createEvent_ShouldReturnEvent() throws Exception {
        EventDTO dto = new EventDTO();
        Event event = new Event();
        event.setId(1L);

        when(eventService.createEvent(any())).thenReturn(event);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() throws Exception {
        EventUpdateDTO dto = new EventUpdateDTO();
        Event event = new Event();
        event.setId(1L);

        when(eventService.updateEvent(eq(1L), any())).thenReturn(event);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cancelEvent_ShouldReturnNoContent() throws Exception {
        CancelEventRequest request = new CancelEventRequest("cancelled");

        doNothing().when(eventService).cancelEvent(eq(1L), any());

        mockMvc.perform(post("/events/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void completeEvent_ShouldReturnNoContent() throws Exception {
        doNothing().when(eventService).completeEvent(1L);

        mockMvc.perform(post("/events/1/complete"))
                .andExpect(status().isNoContent());
    }

    @Test
    void expandMaxParticipants_ShouldReturnNoContent() throws Exception {
        doNothing().when(eventService).expandMaxParticipants(1L, 10);

        mockMvc.perform(post("/events/1/expand")
                        .param("additionalSeats", "10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void trimToSize_ShouldReturnNoContent() throws Exception {
        doNothing().when(eventService).trimToSize(1L);

        mockMvc.perform(post("/events/1/trim-to-size"))
                .andExpect(status().isNoContent());
    }

    @Test
    void closeRegistration_ShouldReturnNoContent() throws Exception {
        doNothing().when(eventService).closeRegistration(1L);

        mockMvc.perform(post("/events/1/close-registration"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getDraftEvents_ShouldReturnList() throws Exception {
        Event event = new Event();
        event.setId(1L);
        event.setStatus(EventStatus.DRAFT);

        when(eventService.getDraftEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getMostPopularEvent_ShouldReturnOptionalEvent() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.getEventWithMostParticipants()).thenReturn(Optional.of(event));

        mockMvc.perform(get("/events/stream/most-popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getGroupedEvents_ShouldReturnMap() throws Exception {
        Event event = new Event();
        event.setId(1L);
        when(eventService.groupEventsByStatus()).thenReturn(Map.of(EventStatus.PUBLISHED, List.of(event)));

        mockMvc.perform(get("/events/stream/grouped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PUBLISHED[0].id").value(1));
    }

    @Test
    void getPartitionedEvents_ShouldReturnMap() throws Exception {
        Event event = new Event();
        event.setId(1L);
        when(eventService.partitionEventsByDate()).thenReturn(Map.of(true, List.of(event), false, List.of()));

        mockMvc.perform(get("/events/stream/partitioned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.true[0].id").value(1));
    }

    @Test
    void searchByKeyword_ShouldReturnEvents() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.searchByKeyword("java")).thenReturn(List.of(event));

        mockMvc.perform(get("/events/catalog/search")
                        .param("keyword", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void filterByStatusFormatLocation_ShouldReturnEvents() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.filterByStatusFormatLocation(EventStatus.PUBLISHED, EventFormat.ONLINE, "Almaty"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/events/catalog/filter")
                        .param("status", "PUBLISHED")
                        .param("format", "ONLINE")
                        .param("location", "Almaty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findEventsInDateRange_ShouldReturnEvents() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.findEventsInDateRange("2024-01-01T00:00:00Z", "2024-12-31T00:00:00Z"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/events/catalog/date-range")
                        .param("from", "2024-01-01T00:00:00Z")
                        .param("to", "2024-12-31T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findEventsWithAvailableSeats_ShouldReturnEvents() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.findEventsWithAvailableSeats(10)).thenReturn(List.of(event));

        mockMvc.perform(get("/events/catalog/available-seats")
                        .param("minSeats", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getUpcomingEventsCatalog_ShouldReturnEvents() throws Exception {
        Event event = new Event();
        event.setId(1L);

        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events/catalog/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
