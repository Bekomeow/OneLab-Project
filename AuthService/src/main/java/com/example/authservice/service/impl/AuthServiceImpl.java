package com.example.authservice.service.impl;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.User;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void authenticateUser(AuthRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Проверяем, существует ли пользователь
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, password));

        // Проверяем пароль
        if (!passwordEncoder.matches(password, user.getPassword())) {
            kafkaTemplate.send("auth.user.login.response",
                    new AuthResponse(user.getId(), email, "INVALID_CREDENTIALS"));
            return;
        }

        // Отправляем успешный логин-респонс
        kafkaTemplate.send("auth.user.login.response",
                new AuthResponse(user.getId(), email, user.getRole().getName()));
    }

    private User registerNewUser(String email, String password) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(userRole);

        return userRepository.save(newUser);
    }
}

