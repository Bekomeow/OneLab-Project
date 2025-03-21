package com.example.authservice.dto;

import com.example.authservice.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Role> roles;
}
