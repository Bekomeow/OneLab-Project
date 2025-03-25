package com.example.eventmanagementservice.listener;

import com.example.commonlibrary.dto.auth.UserDeleteDto;
import com.example.eventmanagementservice.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class UserDeleteListenerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private UserDeleteListener userDeleteListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listen_ShouldCallDeleteAllRegistrationsByUser() {
        UserDeleteDto user = UserDeleteDto.builder()
                .username("test_user")
                .email("test_email")
                .reason("test reason")
                .build();

        userDeleteListener.listen(user);

        verify(registrationService, times(1)).deleteAllRegistrationsByUser("test_user");
    }
}
