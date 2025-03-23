package com.example.eventmanagementservice.service.impl;

import com.example.commonlibrary.dto.event.EventDTO;
import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.commonlibrary.dto.event.EventStatusDto;
import com.example.commonlibrary.dto.event.EventUpdateDTO;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventmanagementservice.entity.Event;
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
import java.time.ZoneId;
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
    private final KafkaTemplate<String, Object> jsonKafkaTemplate;
    private final KafkaTemplate<String, Long> longKafkaTemplate;
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
                .eventFormat(eventDto.getEventFormat())
                .location(eventDto.getLocation())
                .availableSeats(eventDto.getMaxParticipants())
                .build();

        event = eventRepository.save(event);

        EventSearchDto eventSearchDto = EventSearchDto.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventFormat(event.getEventFormat())
                .status(event.getStatus())
                .maxParticipants(event.getMaxParticipants())
                .availableSeats(event.getAvailableSeats())
                .startDate(event.getStartDate().atZone(ZoneId.systemDefault()).toInstant())
                .endDate(event.getEndDate().atZone(ZoneId.systemDefault()).toInstant())
                .build();

        jsonKafkaTemplate.send("event.created", eventSearchDto);

        return event;
    }

    public Event updateEvent(Long id, EventUpdateDTO eventDto) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDto.getTitle());
                    event.setDescription(eventDto.getDescription());

                    eventSearchService.indexEvent(event);

                    EventSearchDto eventSearchDto = EventSearchDto.builder()
                            .eventId(event.getId())
                            .title(event.getTitle())
                            .description(event.getDescription())
                            .build();

                    jsonKafkaTemplate.send("event.updated", eventSearchDto);

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

        EventSearchDto eventSearchDto = EventSearchDto.builder()
                .eventId(event.getId())
                .status(event.getStatus())
                .build();

        jsonKafkaTemplate.send("event.status.notification", notification);
        jsonKafkaTemplate.send("event.updated", eventSearchDto);
    }

    public void cancelEvent(Long eventId, String reason) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.COMPLETED)) {
            throw new IllegalStateException("Событие уже завершено.");
        }

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

            jsonKafkaTemplate.send("event.status.notification", notification);
            longKafkaTemplate.send("event.deleted", event.getId());
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public void closeRegistration(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
            event.setAvailableSeats(0);
            eventRepository.save(event);

            EventSearchDto eventSearchDto = EventSearchDto.builder()
                    .eventId(event.getId())
                    .status(event.getStatus())
                    .build();

            jsonKafkaTemplate.send("event.updated", eventSearchDto);
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public void completeEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.IN_PROGRESS)) {
            throw new IllegalStateException("Только текущие события можно завершить вручную.");
        }

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);

            longKafkaTemplate.send("event.deleted", event.getId());
        } else {
            throw new AccessDeniedException("Недостаточно прав для отмены мероприятия");
        }
    }

    public void expandMaxParticipants(Long eventId, int additionalSeats) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!(event.getStatus().equals(EventStatus.PUBLISHED) || event.getStatus().equals(EventStatus.REGISTRATION_CLOSED))) {
            throw new IllegalStateException("Только для опубликованного события можно расширить максимальное количество участников.");
        }

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {

            if (additionalSeats < 1) {
                throw new IllegalArgumentException("Additional seats must be greater than 0");
            }

            if (event.getStatus().equals(EventStatus.REGISTRATION_CLOSED)) {
                event.setStatus(EventStatus.PUBLISHED);
            }

            event.setMaxParticipants(event.getMaxParticipants() + additionalSeats);
            event.setAvailableSeats(event.getAvailableSeats() + additionalSeats);
            eventRepository.save(event);
        } else {
            throw new AccessDeniedException("Недостаточно прав для расширение максимального количество участников.");
        }
    }

    public void trimToSize(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalStateException("Только для опубликованного события можно сузить максимальное количество участников до количества зарегистрированных участников.");
        }

        if (securityUtil.getCurrentUsername().orElse("").equals(event.getOrganizerName())) {
            int currentParticipants = event.getMaxParticipants() - event.getAvailableSeats();
            event.setMaxParticipants(currentParticipants);
            event.setAvailableSeats(0);
            event.setStatus(EventStatus.REGISTRATION_CLOSED);
            eventRepository.save(event);
        } else {
            throw new AccessDeniedException("Недостаточно прав для расширение максимального количество участников.");
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
