package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("Authorization Header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Authorization header found or invalid format");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token");
            return chain.filter(exchange);
        }

        Claims claims = jwtUtil.extractClaims(token);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        log.info("Roles: {}", roles);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = new User(username, "", authorities);

        log.info("Authorities from token: {}", authorities);

        // Добавляем пользователя в SecurityContext
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(user, null, authorities)
                ));
    }

}

