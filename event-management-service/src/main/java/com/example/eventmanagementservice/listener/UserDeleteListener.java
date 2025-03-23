package com.example.eventmanagementservice.listener;

import com.example.commonlibrary.dto.auth.UserDeleteDto;
import com.example.eventmanagementservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDeleteListener {

    private final RegistrationService registrationService;

    @KafkaListener(
            topics = "auth.user.delete",
            groupId = "event-service-group",
            containerFactory = "userDeleteListenerContainerFactory"
    )
    public void listen(@Payload UserDeleteDto user) {
        registrationService.deleteAllRegistrationsByUser(user.getUsername());
    }
}
