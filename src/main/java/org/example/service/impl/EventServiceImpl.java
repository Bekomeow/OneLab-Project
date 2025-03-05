package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.EventDTO;
import org.example.entity.Event;
import org.example.entity.User;
import org.example.enums.EventStatus;
import org.example.repository.EventRepository;
import org.example.repository.UserRepository;
import org.example.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event createEvent(EventDTO eventDto) {
        User organizer = userRepository.findById(eventDto.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Организатор не найден"));

        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setDate(eventDto.getDate());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizer(organizer);

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
    }

        public void cancelEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findAllByStatusAndDateAfter(EventStatus.PUBLISHED, LocalDateTime.now());
    }
}
