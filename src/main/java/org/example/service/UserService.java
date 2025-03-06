package org.example.service;

import org.example.dto.UserDTO;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    Optional<User> login(String email, String password);
    User getUserById(Long userId);
    List<User> getAllUsers();
}
