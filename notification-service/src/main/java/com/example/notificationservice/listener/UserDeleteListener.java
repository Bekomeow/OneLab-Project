package com.example.notificationservice.listener;

import com.example.commonlibrary.dto.auth.UserDeleteDto;
import com.example.notificationservice.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDeleteListener {

    private final MailSenderService mailSenderService;

    @KafkaListener(
            topics = "auth.user.delete",
            groupId = "notification-service-group",
            containerFactory = "userDeleteListenerContainerFactory"
    )
    public void listen(@Payload UserDeleteDto user) {

        String subject = "Ваш аккаунт удален";
        String body = String.format(
                "Здравствуйте, %s,\n\n" +
                        "Ваш аккаунт был удален.\n\n" +
                        "Причина удаления: %s\n\n" +
                        "Если это ошибка или у вас есть вопросы, пожалуйста, свяжитесь с поддержкой.\n\n" +
                        "С уважением,\n" +
                        "Команда Event Management",
                user.getUsername(),
                user.getReason()
        );

        mailSenderService.send(user.getEmail(), subject, body);

        log.info("Sending account deletion email to user " + user.getEmail());
    }

}
