package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.UserDTO;
import com.example.eventmanagementservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    Optional<User> login(String email, String password);
    User getUserById(Long userId);
    List<User> getAllUsers();
}
