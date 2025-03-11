package com.example.authservice.listener;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthListener {
    private final AuthService authService;

    @KafkaListener(
            topics = "auth.user.login.request",
            groupId = "auth-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenAuthRequest(@Payload AuthRequest request) {
        authService.authenticateUser(request);
    }

}

