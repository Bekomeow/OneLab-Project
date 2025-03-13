package com.example.authservice.dto;

import com.example.authservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private List<Role> roles;
}
