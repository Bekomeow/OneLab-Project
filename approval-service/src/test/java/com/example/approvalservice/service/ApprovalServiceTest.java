package com.example.approvalservice.service;

import com.example.approvalservice.entity.Event;
import com.example.approvalservice.repository.EventRepository;
import com.example.approvalservice.security.SecurityUtil;
import com.example.commonlibrary.enums.event.EventFormat;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaTemplate<String, Object> jsonKafkaTemplate;

    @Mock
    private KafkaTemplate<String, Long> longKafkaTemplate;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private ApprovalService approvalService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Some description")
                .startDate(LocalDateTime.now())
                .organizerName("john_doe")
                .maxParticipants(100)
                .availableSeats(50)
                .location("Almaty")
                .eventFormat(EventFormat.OFFLINE)
                .build();
    }

    @Test
    void testPublish() {
        when(execution.getVariable("eventId")).thenReturn(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(securityUtil.getEmailByUsername("john_doe")).thenReturn("john@example.com");

        approvalService.publish(execution);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCancel() {
        when(execution.getVariable("eventId")).thenReturn(1L);
        when(execution.getVariable("reason")).thenReturn("Cancelled by user");
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(securityUtil.getEmailByUsername("john_doe")).thenReturn("john@example.com");

        approvalService.cancel(execution);

        verify(eventRepository).save(any(Event.class));
    }
}
