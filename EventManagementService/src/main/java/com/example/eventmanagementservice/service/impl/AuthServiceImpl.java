package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.AuthRequest;
import com.example.eventmanagementservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void login(String email, String password) {
        kafkaTemplate.send("auth.user.login.request", new AuthRequest(email, password));
    }
}
