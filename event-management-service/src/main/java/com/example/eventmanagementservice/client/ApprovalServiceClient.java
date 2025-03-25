package com.example.eventmanagementservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "approval-service")
public interface ApprovalServiceClient {

    @PostMapping("/api/approval/start/{eventId}")
    void startApprovalProcess(@PathVariable Long eventId,
                              @RequestHeader("Authorization") String token);
}
