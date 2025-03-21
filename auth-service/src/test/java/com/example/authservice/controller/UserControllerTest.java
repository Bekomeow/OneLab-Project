package com.example.authservice.controller;

import com.example.authservice.entity.User;
import com.example.authservice.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void getEmail_ShouldReturnEmail() throws Exception {
        String username = "testUser";
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userDetailsService.getEmailByUsername(username)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/users/{username}/email", username))
                .andExpect(status().isOk())
                .andExpect(content().string(email));
    }
}
