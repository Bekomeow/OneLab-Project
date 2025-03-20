package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.dto.EventStatusDto;
import com.example.eventmanagementservice.dto.EventUpdateDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.security.SecurityUtil;
import com.example.eventmanagementservice.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventSearchService eventSearchService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SecurityUtil securityUtil;

    @Transactional
    public Event createEvent(EventDTO eventDto) {
        LocalDateTime startDate = Objects.requireNonNull(eventDto.getStartDate(), "Дата начало события не может быть null");
        LocalDateTime endDate = Objects.requireNonNull(eventDto.getEndDate(), "Дата конца события не может быть null");

        if (eventDto.getMaxParticipants() < 0) {
            throw new IllegalArgumentException("Количество участников не может быть отрицательным");
        }

        String organizer = securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован"));

        Event event = Event.builder()
                .title(eventDto.getTitle())
                .description(eventDto.getDescription())
                .startDate(startDate)
                .endDate(endDate)
                .maxParticipants(eventDto.getMaxParticipants())
                .status(EventStatus.DRAFT)
                .organizerName(organizer)
                .build();

        event = eventRepository.save(event);

        try {
            eventSearchService.indexEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить событие в поисковый индекс", e);
        }

        return event;
    }

    public Event updateEvent(Long id, EventUpdateDTO eventDto) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDto.getTitle());
                    event.setDescription(eventDto.getDescription());
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + id));
    }

    public void publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.DRAFT)) {
            throw new IllegalStateException("Мероприятие уже опубликовано");
        }

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

        kafkaTemplate.send("event.status.notification", notification);

    }

    public void cancelEvent(Long eventId, String reason) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName()) || securityUtil.hasRole("ROLE_MODERATOR")) {
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

            kafkaTemplate.send("event.status.notification", notification);
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public void closeRegistration(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
            eventRepository.save(event);
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public void completeEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (event.getStatus() != EventStatus.IN_PROGRESS) {
            throw new IllegalStateException("Только текущие события можно завершить вручную.");
        }

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public List<Event> getUpcomingEvents() {
        List<EventStatus> excludedStatuses = List.of(EventStatus.DRAFT, EventStatus.IN_PROGRESS, EventStatus.COMPLETED, EventStatus.CANCELLED);
        return eventRepository.findUpcomingEvents(excludedStatuses);
    }

    public List<Event> getDraftEvents() {
        return eventRepository.findAllByStatusAndStartDateAfter(EventStatus.DRAFT, LocalDateTime.now());
    }

    public boolean eventExists(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    public List<Event> findEventsByIds(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids).stream()
                .filter(event -> event.getStatus().equals(EventStatus.PUBLISHED))
                .collect(Collectors.toList());
    }

    public Optional<Event> getEventWithMostParticipants() {
        return eventRepository.findAll().stream()
                .reduce((e1, e2) -> e1.getMaxParticipants() > e2.getMaxParticipants() ? e1 : e2);
    }

    public Map<EventStatus, List<Event>> groupEventsByStatus() {
        return eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(Event::getStatus));
    }

    public Map<Boolean, List<Event>> partitionEventsByDate() {
        return eventRepository.findAll().stream()
                .collect(Collectors.partitioningBy(event -> event.getStartDate().isAfter(LocalDateTime.now())));
    }
}
