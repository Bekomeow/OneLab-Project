package com.example.eventmanagementservice.security;

import com.example.eventmanagementservice.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final AuthServiceClient authServiceClient;
    private final JwtUtil jwtUtil;

    public Optional<String> getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        }
        return Optional.empty();
    }

    public String getEmailByUsername(String username) {
        String token = Optional.ofNullable(jwtUtil.getCurrentToken())
                .filter(t -> !t.isBlank())
                .orElseThrow(() -> new IllegalStateException("JWT Token не найден"));

        return Optional.ofNullable(authServiceClient.getEmailByUsername(username, "Bearer " + token))
                .filter(email -> !email.isBlank())
                .orElseThrow(() -> new IllegalStateException("Не удалось получить email пользователя"));
    }

    public Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getAuthorities();
        }
        return List.of();
    }

    public boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    public String getAuthToken() {
        return Optional.ofNullable(jwtUtil.getCurrentToken())
                .filter(t -> !t.isBlank())
                .orElseThrow(() -> new IllegalStateException("JWT Token не найден"));
    }
}
