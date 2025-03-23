package com.example.eventmanagementservice.controller;

import com.example.commonlibrary.dto.event.EventRegisterResponse;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/{eventId}")
    public ResponseEntity<Registration> register(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.registerUserForEvent(eventId));
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<Void> unregisterUser(@PathVariable Long registrationId) {
        registrationService.unregisterUserFromEvent(registrationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<EventRegisterResponse>> registrationsByUser() {
        return ResponseEntity.ok(registrationService.getRegistrationsByUser());
    }
}
