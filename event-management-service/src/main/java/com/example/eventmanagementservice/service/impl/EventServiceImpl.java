package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.dto.EventStatusDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventSearchService eventSearchService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SecurityUtil securityUtil;

    public Event createEvent(EventDTO eventDto) {

        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setDate(eventDto.getDate());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizerName(securityUtil.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("Пользователь не авторизован")));

        eventSearchService.indexEvent(event);

        return eventRepository.save(event);
    }

    public Event updateEvent(EventDTO eventDto) {
        return eventRepository.findById(eventDto.getId())
                .map(event -> {
                    event.setTitle(eventDto.getTitle());
                    event.setDescription(eventDto.getDescription());
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventDto.getId()));
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
                .date(event.getDate())
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
                    .date(event.getDate())
                    .maxParticipants(event.getMaxParticipants())
                    .status("CANCELLED")
                    .reason(reason)
                    .build();

            kafkaTemplate.send("event.status.notification", notification);
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findAllByStatusAndDateAfter(EventStatus.PUBLISHED, LocalDateTime.now());
    }

    public List<Event> getDraftEvents() {
        return eventRepository.findAllByStatusAndDateAfter(EventStatus.DRAFT, LocalDateTime.now());
    }

    public List<Event> getEventsByTitleAndDescription(String query) {
        List<Long> eventIds = eventSearchService.searchEventIds(query);
        return eventRepository.findAllByIdIn(eventIds).stream()
                .filter(event -> event.getStatus().equals(EventStatus.PUBLISHED))
                .collect(Collectors.toList());
    }

    public boolean eventExists(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    public List<Event> findEventsByIds(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

}
