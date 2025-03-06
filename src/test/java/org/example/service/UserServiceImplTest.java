package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.UserDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();

        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setRole(role);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(userDTO);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(registeredUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(registeredUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(registeredUser.getRole()).isEqualTo(role);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(userDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email уже используется");
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registerUser(userDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Роль USER не найдена");
    }

    @Test
    void shouldLoginSuccessfully() {
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        Optional<User> loggedInUser = userService.login(email, password);

        assertThat(loggedInUser).isPresent().contains(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnLogin() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("test@example.com", "password"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь с таким email не найден");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Неверный пароль");
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();

        assertThat(foundUsers).hasSize(2);
    }
}

