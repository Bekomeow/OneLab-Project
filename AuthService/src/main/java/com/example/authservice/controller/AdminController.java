package com.example.authservice.controller;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.enums.Role;
import com.example.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-admin/{username}")
    public ResponseEntity<String> addAdmin(@PathVariable String username) {
        adminService.updateUserRole(username, Role.ADMIN, true);
        return ResponseEntity.ok("Пользователь " + username + " теперь администратор.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-moderator/{username}")
    public ResponseEntity<String> addModerator(@PathVariable String username) {
        adminService.updateUserRole(username, Role.MODERATOR, true);
        return ResponseEntity.ok("Пользователь " + username + " теперь модератор.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        adminService.deleteUser(username);
        return ResponseEntity.ok("Пользователь " + username + " удален.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update-role/{username}")
    public ResponseEntity<String> updateUserRole(
            @PathVariable String username,
            @RequestParam Role role,
            @RequestParam boolean add) {

        adminService.updateUserRole(username, role, add);

        String action = add ? "добавлена" : "удалена";
        return ResponseEntity.ok("Роль " + role + " была " + action + " у пользователя " + username);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}

