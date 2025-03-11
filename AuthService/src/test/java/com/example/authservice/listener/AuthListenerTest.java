package com.example.authservice.listener;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthListenerTest {

    private AuthService authService;
    private AuthListener authListener;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authListener = new AuthListener(authService);
    }

    @Test
    void listenAuthRequest_ShouldCallAuthenticateUser() {
        AuthRequest request = new AuthRequest("user@example.com", "password123");

        authListener.listenAuthRequest(request);

        ArgumentCaptor<AuthRequest> captor = ArgumentCaptor.forClass(AuthRequest.class);
        verify(authService, times(1)).authenticateUser(captor.capture());

        assertThat(captor.getValue()).isEqualTo(request);
    }
}
