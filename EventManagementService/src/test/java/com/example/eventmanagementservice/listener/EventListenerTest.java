package com.example.eventmanagementservice.listener;

import com.example.eventmanagementservice.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class EventListenerTest {

    private EventListener eventListener;

    @BeforeEach
    void setUp() {
        eventListener = new EventListener();
        SecurityContextHolder.clearContext();
    }

    @Test
    void handleAuthResponse_ShouldAddResponseToQueueAndSetAuthentication() throws InterruptedException {
        AuthResponse response = new AuthResponse(1L, "user@example.com", "USER");

        eventListener.handleAuthResponse(response);

        AuthResponse result = eventListener.waitForResponse(1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getName()).isEqualTo("user@example.com");
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactly("USER");
    }

    @Test
    void waitForResponse_ShouldReturnNull_WhenQueueIsEmpty() throws InterruptedException {
        AuthResponse result = eventListener.waitForResponse(500, TimeUnit.MILLISECONDS);

        assertThat(result).isNull();
    }
}
