package com.example.eventmanagementservice.controller;

import com.example.commonlibrary.dto.event.EventRegisterResponse;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.service.RegistrationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void register_ShouldReturnRegistration() throws Exception {
        Long eventId = 1L;
        Registration mockRegistration = new Registration();
        mockRegistration.setId(1L);
        mockRegistration.setUsername("testUser");

        when(registrationService.registerUserForEvent(eventId)).thenReturn(mockRegistration);

        mockMvc.perform(MockMvcRequestBuilders.post("/events/registrations/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(registrationService, times(1)).registerUserForEvent(eventId);
    }

    @Test
    void unregisterUser_ShouldReturnNoContent() throws Exception {
        Long registrationId = 1L;
        doNothing().when(registrationService).unregisterUserFromEvent(registrationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/registrations/{registrationId}", registrationId))
                .andExpect(status().isNoContent());

        verify(registrationService, times(1)).unregisterUserFromEvent(registrationId);
    }

    @Test
    void registrationsByUser_ShouldReturnRegistrationList() throws Exception {
        List<EventRegisterResponse> mockRegistrations = List.of(
                new EventRegisterResponse(1L, "testUser", "test title")
        );

        when(registrationService.getRegistrationsByUser()).thenReturn(mockRegistrations);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/registrations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].eventTitle").value("test title"));

        verify(registrationService, times(1)).getRegistrationsByUser();
    }
}
