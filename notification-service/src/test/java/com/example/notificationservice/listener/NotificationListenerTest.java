package com.example.notificationservice.listener;

import com.example.commonlibrary.dto.event.EventRegistrationDto;
import com.example.commonlibrary.dto.event.EventStatusDto;
import com.example.notificationservice.service.MailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private MailSenderService mailSenderService;
    @InjectMocks
    private RegistrationListener notificationListener;
    private EventRegistrationDto registration;
    @InjectMocks
    private EventStatusListener eventStatusListener;
    private EventStatusDto publishedEvent;
    private EventStatusDto cancelledEvent;

    @BeforeEach
    void setUp() {
        registration = EventRegistrationDto.builder()
                .email("test@example.com")
                .title("Spring Boot Workshop")
                .description("An advanced workshop on Spring Boot.")
                .date(LocalDateTime.of(2025, Month.MARCH, 15, 10, 0))
                .maxParticipants(50)
                .build();

        publishedEvent = EventStatusDto.builder()
                .email("test@example.com")
                .title("Spring Boot Workshop")
                .description("An advanced workshop on Spring Boot.")
                .date(LocalDateTime.of(2025, Month.MARCH, 15, 10, 0))
                .maxParticipants(50)
                .status("PUBLISHED")
                .build();

        cancelledEvent = EventStatusDto.builder()
                .email("test@example.com")
                .title("Spring Boot Workshop")
                .description("An advanced workshop on Spring Boot.")
                .date(LocalDateTime.of(2025, Month.MARCH, 15, 10, 0))
                .maxParticipants(50)
                .status("CANCELLED")
                .reason("Insufficient registrations")
                .build();
    }

    @Test
    void listen_ShouldCallMailSenderService() {
        notificationListener.listen(registration);

        verify(mailSenderService, times(1)).send(
                eq("test@example.com"),
                eq("Регистрации на событие: Spring Boot Workshop"),
                anyString()
        );
    }

    @Test
    void listen_ShouldLogMessageRegistration() {
        notificationListener.listen(registration);

        System.out.println("Тест: проверка логирования");
        Mockito.verify(mailSenderService).send(anyString(), anyString(), anyString());
    }

    @Test
    void listen_ShouldSendPublishedEventEmail() {
        eventStatusListener.listen(publishedEvent);

        verify(mailSenderService, times(1)).send(
                eq("test@example.com"),
                contains("Изменение статуса события: Spring Boot Workshop (опубликовано)"),
                anyString()
        );
    }

    @Test
    void listen_ShouldSendCancelledEventEmail() {
        eventStatusListener.listen(cancelledEvent);

        verify(mailSenderService, times(1)).send(
                eq("test@example.com"),
                contains("Изменение статуса события: Spring Boot Workshop (отменено)"),
                contains("Причина отмены: Insufficient registrations")
        );
    }

    @Test
    void listen_ShouldLogMessagePublishedEvent() {
        eventStatusListener.listen(publishedEvent);

        System.out.println("Тест: проверка логирования");
        Mockito.verify(mailSenderService).send(anyString(), anyString(), anyString());
    }
}
