package com.example.eventsearchservice.listener;

import com.example.commonlibrary.dto.event.EventSearchDto;
import com.example.eventsearchservice.service.EventSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListener {

    private final EventSearchService eventSearchService;

    @KafkaListener(
            topics = "event.created",
            groupId = "catalog-group",
            containerFactory = "eventListenerContainerFactory"
    )
    public void handleEventCreated(@Payload EventSearchDto event) {
        eventSearchService.saveEvent(event);
    }

    @KafkaListener(
            topics = "event.updated",
            groupId = "catalog-group",
            containerFactory = "eventListenerContainerFactory"
    )
    public void handleEventUpdated(@Payload EventSearchDto event) {
        eventSearchService.updateEvent(event);
    }

    @KafkaListener(
            topics = "event.deleted",
            groupId = "catalog-group",
            containerFactory = "deleteEventListenerContainerFactory"
    )
    public void handleEventDeleted(@Payload Long eventId) {
        eventSearchService.deleteEvent(eventId);
    }
}
