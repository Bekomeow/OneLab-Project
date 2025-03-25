package com.example.approvalservice.controller;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final RuntimeService runtimeService;

    @PostMapping("/start/{eventId}")
    public ResponseEntity<String> startApproval(@PathVariable Long eventId) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("eventId", eventId);
        runtimeService.startProcessInstanceByKey("event_approval_process", vars);
        return ResponseEntity.ok("Approval process started");
    }
}
