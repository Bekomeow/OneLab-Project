package com.example.eventmanagementservice.config;

import com.example.eventmanagementservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/events/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/events").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/events/{eventId}/expand", "/events/{eventId}/trim-to-size").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.PUT, "/events").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/events/{eventId}/publish").hasAuthority("ROLE_MODERATOR")
                        .requestMatchers(HttpMethod.POST, "/events/{eventId}/cancel").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/events/upcoming").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/events/filter/**").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/events/stream/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/events/drafts").hasAuthority("ROLE_MODERATOR")
                        .requestMatchers(HttpMethod.POST, "/events/registrations/{eventId}").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.DELETE, "/events/registrations/{registrationId}").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/events/registrations").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/events/performance/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

