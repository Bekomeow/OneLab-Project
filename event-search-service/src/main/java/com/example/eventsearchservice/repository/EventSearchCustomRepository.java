package com.example.eventsearchservice.repository;

import com.example.commonlibrary.enums.event.EventFormat;
import com.example.commonlibrary.enums.event.EventStatus;
import com.example.eventsearchservice.model.EventDocument;

import java.util.List;

public interface EventSearchCustomRepository {
    List<EventDocument> findByDynamicFilters(EventStatus status, EventFormat format, String location);
}

