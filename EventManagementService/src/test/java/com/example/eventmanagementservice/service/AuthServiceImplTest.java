package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.AuthRequest;
import com.example.eventmanagementservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthService authService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(kafkaTemplate);
    }

    @Test
    void login_ShouldSendAuthRequestToKafka() {
        String email = "user@example.com";
        String password = "securePassword";

        authService.login(email, password);

        ArgumentCaptor<AuthRequest> authRequestCaptor = ArgumentCaptor.forClass(AuthRequest.class);
        verify(kafkaTemplate, times(1)).send(eq("auth.user.login.request"), authRequestCaptor.capture());

        AuthRequest capturedRequest = authRequestCaptor.getValue();
        assertThat(capturedRequest.getEmail()).isEqualTo(email);
        assertThat(capturedRequest.getPassword()).isEqualTo(password);
    }
}

