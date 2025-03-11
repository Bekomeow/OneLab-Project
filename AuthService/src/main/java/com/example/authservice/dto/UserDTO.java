package com.example.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Long roleId;
}
