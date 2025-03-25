package com.example.approvalservice.worker;

import com.example.approvalservice.entity.Event;
import com.example.approvalservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataValidationDelegate implements JavaDelegate {

    private final EventRepository eventRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long eventId = (Long) execution.getVariable("eventId");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));

        boolean valid = event.getTitle() != null && !event.getTitle().isBlank()
                && event.getDescription() != null && !event.getDescription().isBlank()
                && event.getAvailableSeats() > 0
                && event.getMaxParticipants() > 0
                && event.getLocation() != null
                && event.getStartDate().isBefore(event.getEndDate());

        if (!valid) {
            throw new RuntimeException("Event validation failed: " + eventId);
        }
    }
}
