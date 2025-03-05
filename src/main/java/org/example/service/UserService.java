package org.example.service;

import org.example.dto.UserDTO;
import org.example.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User getUserById(Long userId);
    List<User> getAllUsers();
}
