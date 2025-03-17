package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventStatusDto {
    private String email;           // Почта пользователя
    private String title;           // Название мероприятия
    private String description;     // Описание
    private LocalDateTime date;     // Дата мероприятия
    private int maxParticipants;    // Количество мест
    private String status;          // (PUBLISHED или CANCELLED)
    private String reason;          // Причина отмены (если отменено)
}
