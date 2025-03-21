package com.example.notificationservice.listener;

import com.example.notificationservice.dto.EventStatusDto;
import com.example.notificationservice.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventStatusListener {
    private final MailSenderService mailSenderService;

    @KafkaListener(
            topics = "event.status.notification",
            groupId = "notification-service-group",
            containerFactory = "eventStatusListenerContainerFactory"
    )
    public void listen(@Payload EventStatusDto update) {

        String statusMessage = "PUBLISHED".equals(update.getStatus()) ? "опубликовано" : "отменено";
        String subject = "Изменение статуса события: " + update.getTitle() + " (" + statusMessage + ")";

        String body;
        if ("CANCELLED".equals(update.getStatus())) {
            body = String.format(
                    "Здравствуйте,\n\n" +
                            "Ваше мероприятие \"%s\" было отменено.\n\n" +
                            "Причина отмены: %s\n\n" +
                            "Описание: %s\n" +
                            "Дата и время: %s\n" +
                            "Максимальное количество участников: %d\n\n" +
                            "С уважением,\n" +
                            "Команда Event Management",
                    update.getTitle(),
                    update.getReason() != null ? update.getReason() : "Причина не указана",
                    update.getDescription(),
                    update.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")),
                    update.getMaxParticipants()
            );
        } else {
            body = String.format(
                    "Здравствуйте,\n\n" +
                            "Ваше мероприятие \"%s\" было опубликовано.\n\n" +
                            "Описание: %s\n" +
                            "Дата и время: %s\n" +
                            "Максимальное количество участников: %d\n\n" +
                            "С уважением,\n" +
                            "Команда Event Management",
                    update.getTitle(),
                    update.getDescription(),
                    update.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")),
                    update.getMaxParticipants()
            );
        }

        mailSenderService.send(update.getEmail(), subject, body);
        log.info("Sent event status email to " + update.getEmail() + " for event " + update.getTitle());
    }
}
