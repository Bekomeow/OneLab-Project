package com.example.eventmanagementservice.service;

import com.example.eventmanagementservice.dto.RegistrationDTO;
import com.example.eventmanagementservice.entity.Registration;

import java.util.List;

public interface RegistrationService {
    Registration registerUserForEvent(Long eventId);
    void unregisterUserFromEvent(Long registrationId);
    List<RegistrationDTO> getRegistrationsByUser();
    void deleteAllRegistrationsByUser(String Username);
}
