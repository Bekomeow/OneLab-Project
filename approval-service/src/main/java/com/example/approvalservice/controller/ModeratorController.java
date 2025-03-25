package com.example.approvalservice.controller;

import com.example.commonlibrary.dto.event.CancelEventRequest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ModeratorController {

    private final TaskService taskService;
    private final RuntimeService runtimeService;

    @PostMapping("/{eventId}/approve")
    public ResponseEntity<String> approve(@PathVariable Long eventId) {
        return completeModeratorTask(eventId, true, "");
    }

    @PostMapping("/{eventId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long eventId,
                                         @RequestBody CancelEventRequest request) {
        return completeModeratorTask(eventId, false, request.getReason());
    }

    private ResponseEntity<String> completeModeratorTask(Long eventId, boolean approved, String reason) {
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .variableValueEquals("eventId", eventId)
                .list();

        if (instances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No approval process found for eventId=" + eventId);
        }

        String processInstanceId = instances.get(0).getId();

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskName("Moderator Approval")
                .active()
                .list();

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending moderator task found.");
        }

        Task task = tasks.get(0);

        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", approved ? Boolean.TRUE : Boolean.FALSE);
        if (!approved && reason != null) {
            vars.put("reason", reason);
        }

        taskService.complete(task.getId(), vars);

        return ResponseEntity.ok(approved ? "Event approved" : "Event rejected");
    }

}
