package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.User;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, kafkaTemplate);
    }

    @Test
    void authenticateUser_ShouldAuthenticateSuccessfully() {
        // Arrange
        AuthRequest request = new AuthRequest("user@example.com", "securePassword");
        Role role = new Role(1L, "USER");
        User user = new User(1L, "user@example.com", "encodedPassword", role);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("securePassword", "encodedPassword")).thenReturn(true);

        // Act
        authService.authenticateUser(request);

        // Assert
        ArgumentCaptor<AuthResponse> responseCaptor = ArgumentCaptor.forClass(AuthResponse.class);
        verify(kafkaTemplate, times(1)).send(eq("auth.user.login.response"), responseCaptor.capture());

        AuthResponse capturedResponse = responseCaptor.getValue();
        assertThat(capturedResponse.getId()).isEqualTo(1L);
        assertThat(capturedResponse.getEmail()).isEqualTo("user@example.com");
        assertThat(capturedResponse.getRole()).isEqualTo("USER");
    }

    @Test
    void authenticateUser_ShouldSendInvalidCredentials_WhenPasswordDoesNotMatch() {
        // Arrange
        AuthRequest request = new AuthRequest("user@example.com", "wrongPassword");
        User user = new User(1L, "user@example.com", "encodedPassword", new Role(1L, "USER"));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        authService.authenticateUser(request);

        // Assert
        ArgumentCaptor<AuthResponse> responseCaptor = ArgumentCaptor.forClass(AuthResponse.class);
        verify(kafkaTemplate, times(1)).send(eq("auth.user.login.response"), responseCaptor.capture());

        AuthResponse capturedResponse = responseCaptor.getValue();
        assertThat(capturedResponse.getRole()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    void authenticateUser_ShouldRegisterNewUser_WhenEmailNotFound() {
        AuthRequest request = new AuthRequest("newuser@example.com", "newPassword");
        Role role = new Role(1L, "USER");
        User newUser = new User(2L, "newuser@example.com", "encodedNewPassword", role);

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        authService.authenticateUser(request);

        verify(userRepository, times(1)).save(any(User.class));
        ArgumentCaptor<AuthResponse> responseCaptor = ArgumentCaptor.forClass(AuthResponse.class);
        verify(kafkaTemplate, times(1)).send(eq("auth.user.login.response"), responseCaptor.capture());

        AuthResponse capturedResponse = responseCaptor.getValue();
        assertThat(capturedResponse.getId()).isEqualTo(2L);
        assertThat(capturedResponse.getEmail()).isEqualTo("newuser@example.com");
    }
}
