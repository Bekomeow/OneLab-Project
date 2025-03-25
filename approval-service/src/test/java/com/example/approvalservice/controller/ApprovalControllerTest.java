package com.example.approvalservice.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApprovalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RuntimeService runtimeService;

    @Test
    void startApproval_ShouldStartProcessAndReturnSuccessMessage() throws Exception {
        Long eventId = 42L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/approval/start/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().string("Approval process started"));

        verify(runtimeService).startProcessInstanceByKey(
                eq("event_approval_process"),
                argThat((Map<String, Object> vars) -> eventId.equals(vars.get("eventId")))
        );
    }
}
