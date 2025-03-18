package com.example.apigateway.config;

import com.example.apigateway.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthFilter jwtAuthFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.GET, "/auth/admin/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/events").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.PUT, "/events").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.POST, "/events/{eventId}/publish").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.POST, "/events/{eventId}/cancel").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER")
                        .pathMatchers(HttpMethod.GET, "/events/upcoming").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.GET, "/events/filter/**").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.GET, "/events/stream/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/events/drafts").hasAuthority("ROLE_MODERATOR")
                        .pathMatchers(HttpMethod.POST, "/events/registrations/{eventId}").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.DELETE, "/events/registrations/{registrationId}").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.GET, "/events/registrations").hasAuthority("ROLE_USER")
                        .pathMatchers(HttpMethod.GET, "/events/performance/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
