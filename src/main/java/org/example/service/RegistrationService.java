package org.example.service;

import org.example.entity.Registration;

public interface RegistrationService {
    Registration registerUserForEvent(Long userId, Long eventId);
    void unregisterUserFromEvent(Long registrationId);
}
