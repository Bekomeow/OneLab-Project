package com.example.authservice.security;

import com.example.authservice.entity.User;
import com.example.authservice.enums.Role;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("Admin").isEmpty()) {
            User admin = User.builder()
                    .username("Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .roles(List.of(Role.USER, Role.MODERATOR, Role.ADMIN))
                    .build();
            userRepository.save(admin);
            System.out.println("Администратор создан!");
        } else {
            System.out.println("Администратор уже существует.");
        }
    }
}
