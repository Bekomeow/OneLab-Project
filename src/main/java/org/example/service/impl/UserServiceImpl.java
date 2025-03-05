package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
