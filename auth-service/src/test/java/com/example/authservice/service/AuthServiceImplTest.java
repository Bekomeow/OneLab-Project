package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.User;
import com.example.authservice.enums.Role;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.JwtUtil;
import com.example.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testUser", "test@example.com", "password");

        authRequest = new AuthRequest("testUser", "password");
        user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Collections.singletonList(Role.USER))
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mockedToken");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockedToken");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mockedToken");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, authRequest.getPassword()));

        AuthResponse response = authService.authenticate(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockedToken");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));

        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }
}
