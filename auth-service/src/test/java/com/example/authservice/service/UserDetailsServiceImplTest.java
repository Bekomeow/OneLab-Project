package com.example.authservice.service;

import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        var loadedUser = userDetailsService.loadUserByUsername("testUser");

        assertThat(loadedUser).isEqualTo(user);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknownUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(userRepository, times(1)).findByUsername("unknownUser");
    }

    @Test
    void shouldGetEmailByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        var userDetails = userDetailsService.getEmailByUsername("testUser");

        assertThat(userDetails).isEqualTo(user);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.getEmailByUsername("unknownUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(userRepository, times(1)).findByUsername("unknownUser");
    }

    @Test
    void shouldGetCurrentUsername() {
        var authentication = mock(org.springframework.security.core.Authentication.class);
        var securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        String currentUsername = userDetailsService.getCurrentUsername();

        assertThat(currentUsername).isEqualTo("testUser");
    }

    @Test
    void shouldReturnNullWhenNoAuthentication() {
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        String currentUsername = userDetailsService.getCurrentUsername();

        assertThat(currentUsername).isNull();
    }
}
