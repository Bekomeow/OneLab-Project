package com.example.eventmanagementservice.service.impl;

import com.example.eventmanagementservice.dto.UserDTO;
import com.example.eventmanagementservice.entity.Role;
import com.example.eventmanagementservice.entity.User;
import com.example.eventmanagementservice.repository.RoleRepository;
import com.example.eventmanagementservice.repository.UserRepository;
import com.example.eventmanagementservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public User registerUser(UserDTO userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Роль USER не найдена"));

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким email не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        return Optional.of(user);
    }


    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
