package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;

public interface AuthService {
    void authenticateUser(AuthRequest request);
}
