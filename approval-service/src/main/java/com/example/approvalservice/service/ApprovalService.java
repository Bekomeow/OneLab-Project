package com.example.approvalservice.service;

import com.example.approvalservice.entity.Event;
import com.example.approvalservice.repository.EventRepository;
import com.example.approvalservice.security.SecurityUtil;
import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.dto.event.EventStatusDto;
import com.example.commonlibrary.enums.event.EventStatus;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("approvalService")
@RequiredArgsConstructor
public class ApprovalService {

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> jsonKafkaTemplate;
    private final KafkaTemplate<String, Long> longKafkaTemplate;
    private final SecurityUtil securityUtil;

    public void publish(DelegateExecution execution) {
        Long eventId = (Long) execution.getVariable("eventId");
        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setStatus(EventStatus.PUBLISHED);
        eventRepository.save(event);

        String email = securityUtil.getEmailByUsername(event.getOrganizerName());

        EventStatusDto notification = EventStatusDto.builder()
                .email(email)
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getStartDate())
                .maxParticipants(event.getMaxParticipants())
                .status("PUBLISHED")
                .build();

        EventSearchDto eventSearchDto = EventSearchDto.builder()
                .eventId(event.getId())
                .status(event.getStatus())
                .build();

        jsonKafkaTemplate.send("event.status.notification", notification);
        jsonKafkaTemplate.send("event.updated", eventSearchDto);
    }

    public void cancel(DelegateExecution execution) {
        Long eventId = (Long) execution.getVariable("eventId");
        String reason = (String) execution.getVariable("reason");

        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);

        String email = securityUtil.getEmailByUsername(event.getOrganizerName());

        EventStatusDto notification = EventStatusDto.builder()
                .email(email)
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getStartDate())
                .maxParticipants(event.getMaxParticipants())
                .status("CANCELLED")
                .reason(reason)
                .build();

        jsonKafkaTemplate.send("event.status.notification", notification);
        longKafkaTemplate.send("event.deleted", event.getId());
    }
}
