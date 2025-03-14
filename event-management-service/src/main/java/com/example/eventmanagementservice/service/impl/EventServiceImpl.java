package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.enums.EventStatus;
import com.example.eventmanagementservice.repository.EventRepository;
import com.example.eventmanagementservice.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return null;
    }

    public Event createEvent(EventDTO eventDto) {

        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setDate(eventDto.getDate());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizerName(getCurrentUsername());

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

    public List<Event> getDraftEvents() {
        return eventRepository.findAllByStatusAndDateAfter(EventStatus.DRAFT, LocalDateTime.now());
    }

    public boolean eventExists(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    public List<Event> findEventsByIds(List<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

}
