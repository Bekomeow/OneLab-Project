package com.example.commonlibrary.dto.auth;

import com.example.commonlibrary.enums.auth.Role;
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
