package com.example.authservice.controller;

import com.example.authservice.entity.User;
import com.example.authservice.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/{username}/email")
    public ResponseEntity<String> getEmail(@PathVariable String username) {
        return ResponseEntity.ok(((User) userDetailsService.getEmailByUsername(username)).getEmail());
    }

}
