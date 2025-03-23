package com.example.authservice.service;

import com.example.commonlibrary.dto.auth.AuthRequest;
import com.example.commonlibrary.dto.auth.AuthResponse;
import com.example.commonlibrary.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
