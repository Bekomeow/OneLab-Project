package com.example.eventmanagementservice.event.listener;


import com.example.eventmanagementservice.event.EventCreatedEvent;
import com.example.eventmanagementservice.client.ApprovalServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedListener {

    private final ApprovalServiceClient approvalServiceClient;

    @TransactionalEventListener
    public void onEventCreated(EventCreatedEvent event) {
        approvalServiceClient.startApprovalProcess(event.getEvent().getId(), event.getToken());
        log.info("EventCreatedListener received event for ID: {}", event.getEvent().getId());
    }

}
