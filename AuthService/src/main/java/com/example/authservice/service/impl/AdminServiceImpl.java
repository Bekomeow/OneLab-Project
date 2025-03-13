package com.example.authservice.service.impl;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.entity.User;
import com.example.authservice.enums.Role;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserRole(String username, Role role, boolean addRole) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        if (addRole) {
            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            } else {
                throw new IllegalArgumentException("У пользователя уже есть эта роль.");
            }
        } else {
            if (user.getRoles().size() == 1) {
                throw new IllegalArgumentException("Нельзя удалить последнюю роль пользователя.");
            }
            if (!user.getRoles().contains(role)) {
                throw new IllegalArgumentException("У пользователя нет такой роли.");
            }
            user.getRoles().remove(role);
        }

        userRepository.save(user);
    }


    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new AccessDeniedException("Нельзя удалить администратора!");
        }

        userRepository.delete(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getUsername(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
    }
}


