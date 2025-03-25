package com.example.eventmanagementservice.event;

import com.example.eventmanagementservice.entity.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventCreatedEvent extends ApplicationEvent {
    private final Event event;
    private final String token;

    public EventCreatedEvent(Event event, String token) {
        super(event);
        this.event = event;
        this.token = token;
    }
}
