package com.example.eventmanagementservice.service.impl;

import com.example.commonlibrary.enums.event.EventStatus;
import com.example.commonlibrary.enums.event.TicketStatus;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.service.EventStatusUpdaterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventStatusUpdaterServiceImpl implements EventStatusUpdaterService {

    private final EventRepository eventRepository;

    @Scheduled(fixedRate = 60000) // Проверка каждую минуту
    @Transactional
    public void updateEventStatuses() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        List<Event> toInProgress = eventRepository.findAllByStatusInAndStartDateBefore(
                List.of(EventStatus.PUBLISHED, EventStatus.REGISTRATION_CLOSED), now
        );
        toInProgress.forEach(event -> event.setStatus(EventStatus.IN_PROGRESS));
        toInProgress.forEach(event -> {
            event.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.USED));
        });

        eventRepository.saveAll(toInProgress);

        List<Event> toCompleted = eventRepository.findByStatusAndEndDateBefore(EventStatus.IN_PROGRESS, now);
        toCompleted.forEach(event -> {
            event.setStatus(EventStatus.COMPLETED);
            event.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.USED));
        });

        eventRepository.saveAll(toCompleted);
    }
}
