package com.example.approvalservice.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ModeratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RuntimeService runtimeService;

    @MockBean
    private TaskService taskService;

    @Test
    void approve_ShouldReturnSuccess() throws Exception {
        Long eventId = 1L;
        String processInstanceId = "proc1";
        String taskId = "task1";

        ProcessInstance instance = mock(ProcessInstance.class);
        when(instance.getId()).thenReturn(processInstanceId);

        ProcessInstanceQuery piQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(piQuery);
        when(piQuery.variableValueEquals("eventId", eventId)).thenReturn(piQuery);
        when(piQuery.list()).thenReturn(List.of(instance));

        Task task = mock(Task.class);
        when(task.getId()).thenReturn(taskId);

        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(processInstanceId)).thenReturn(taskQuery);
        when(taskQuery.taskName("Moderator Approval")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/approval/{eventId}/approve", eventId))
                .andExpect(status().isOk())
                .andExpect(content().string("Event approved"));

        verify(taskService).complete(eq(taskId), argThat(vars -> Boolean.TRUE.equals(vars.get("approved"))));
    }

    @Test
    void reject_ShouldReturnRejected() throws Exception {
        Long eventId = 2L;
        String reason = "Not suitable";
        String processInstanceId = "proc2";
        String taskId = "task2";

        ProcessInstance instance = mock(ProcessInstance.class);
        when(instance.getId()).thenReturn(processInstanceId);

        ProcessInstanceQuery piQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(piQuery);
        when(piQuery.variableValueEquals("eventId", eventId)).thenReturn(piQuery);
        when(piQuery.list()).thenReturn(List.of(instance));

        Task task = mock(Task.class);
        when(task.getId()).thenReturn(taskId);

        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(processInstanceId)).thenReturn(taskQuery);
        when(taskQuery.taskName("Moderator Approval")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/approval/{eventId}/reject", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"" + reason + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Event rejected"));

        verify(taskService).complete(eq(taskId), argThat(vars ->
                Boolean.FALSE.equals(vars.get("approved")) && reason.equals(vars.get("reason"))
        ));
    }

    @Test
    void approve_WhenNoProcessFound_ShouldReturn404() throws Exception {
        ProcessInstanceQuery piQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(piQuery);
        when(piQuery.variableValueEquals("eventId", 99L)).thenReturn(piQuery);
        when(piQuery.list()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/approval/99/approve"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No approval process found for eventId=99"));
    }

    @Test
    void approve_WhenNoTaskFound_ShouldReturn404() throws Exception {
        Long eventId = 3L;
        String processInstanceId = "proc3";

        ProcessInstance instance = mock(ProcessInstance.class);
        when(instance.getId()).thenReturn(processInstanceId);

        ProcessInstanceQuery piQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(piQuery);
        when(piQuery.variableValueEquals("eventId", eventId)).thenReturn(piQuery);
        when(piQuery.list()).thenReturn(List.of(instance));

        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(processInstanceId)).thenReturn(taskQuery);
        when(taskQuery.taskName("Moderator Approval")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/approval/{eventId}/approve", eventId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No pending moderator task found."));
    }
}
