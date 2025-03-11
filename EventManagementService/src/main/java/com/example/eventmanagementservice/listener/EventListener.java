package com.example.eventmanagementservice.listener;

import com.example.eventmanagementservice.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EventListener {
    private final BlockingQueue<AuthResponse> responseQueue = new LinkedBlockingQueue<>();

    @KafkaListener(
            topics = "auth.user.login.response",
            groupId = "event-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAuthResponse(AuthResponse response) {
        responseQueue.offer(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(response.getEmail(), null, List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public AuthResponse waitForResponse(long timeout, TimeUnit unit) throws InterruptedException {
        return responseQueue.poll(timeout, unit);
    }
}
