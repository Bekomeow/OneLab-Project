package com.example.authservice.service;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.enums.Role;

import java.util.List;

public interface AdminService {
    void updateUserRole(String username, Role role, boolean addRole);
    void deleteUser(String username);
    List<UserResponse> getAllUsers();
}
