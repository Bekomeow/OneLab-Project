package com.example.authservice.service;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.entity.User;
import com.example.authservice.enums.Role;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setRoles(List.of(Role.USER));
    }


    @Test
    void shouldThrowException_WhenRemovingLastRole() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.updateUserRole("testUser", Role.USER, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Нельзя удалить последнюю роль пользователя.");
    }


    @Test
    void shouldDeleteUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        adminService.deleteUser("testUser");

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowException_WhenDeletingAdmin() {
        user.setRoles(List.of(Role.ADMIN));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.deleteUser("testUser"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Нельзя удалить администратора!");
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> users = adminService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("testUser");
    }
}
