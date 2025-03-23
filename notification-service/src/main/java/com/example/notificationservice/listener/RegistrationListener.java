package com.example.notificationservice.listener;

import com.example.commonlibrary.dto.event.EventRegistrationDto;
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
public class RegistrationListener {
    private final MailSenderService mailSenderService;

    @KafkaListener(
            topics = "event.registration.created",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Payload EventRegistrationDto registration) {

        String subject = "Регистрации на событие: " + registration.getTitle();
        String body = String.format(
                "Здравствуйте,\n\n" +
                        "Вы зарегистрированы на мероприятие \"%s\".\n\n" +
                        "Описание: %s\n" +
                        "Дата и время: %s\n" +
                        "Максимальное количество участников: %d\n\n" +
                        "С уважением,\n" +
                        "Команда Event Management",
                registration.getTitle(),
                registration.getDescription(),
                registration.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")),
                registration.getMaxParticipants()
        );

        mailSenderService.send(registration.getEmail(), subject, body);

        log.info("Sending email to user " + registration.getEmail() + " for event " + registration.getTitle());
    }

}
