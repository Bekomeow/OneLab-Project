package com.example.authservice.service;

import com.example.commonlibrary.dto.auth.UserResponse;
import com.example.commonlibrary.enums.auth.Role;

import java.util.List;

public interface AdminService {
    void updateUserRole(String username, Role role, boolean addRole);
    void deleteUser(String username, String reason);
    List<UserResponse> getAllUsers();
}
