package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.entity.Registration;

public interface RegistrationService {
    Registration registerUserForEvent(Long userId, String userEmail, Long eventId);
    void unregisterUserFromEvent(Long registrationId);
}
