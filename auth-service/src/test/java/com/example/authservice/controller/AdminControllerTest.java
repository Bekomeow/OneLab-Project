package com.example.authservice.controller;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.enums.Role;
import com.example.authservice.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void addAdmin_ShouldReturnSuccessMessage() throws Exception {
        String username = "testUser";

        doNothing().when(adminService).updateUserRole(username, Role.ADMIN, true);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin/add-admin/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь " + username + " теперь администратор."));

        verify(adminService, times(1)).updateUserRole(username, Role.ADMIN, true);
    }

    @Test
    void addModerator_ShouldReturnSuccessMessage() throws Exception {
        String username = "testUser";

        doNothing().when(adminService).updateUserRole(username, Role.MODERATOR, true);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin/add-moderator/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь " + username + " теперь модератор."));

        verify(adminService, times(1)).updateUserRole(username, Role.MODERATOR, true);
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() throws Exception {
        String username = "testUser";

        doNothing().when(adminService).deleteUser(username);

        mockMvc.perform(MockMvcRequestBuilders.delete("/auth/admin/delete-user/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь " + username + " удален."));

        verify(adminService, times(1)).deleteUser(username);
    }

    @Test
    void updateUserRole_ShouldReturnSuccessMessage() throws Exception {
        String username = "testUser";
        Role role = Role.MODERATOR;
        boolean add = true;

        doNothing().when(adminService).updateUserRole(username, role, add);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin/update-role/{username}", username)
                        .param("role", role.name())
                        .param("add", String.valueOf(add))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Роль " + role + " была добавлена у пользователя " + username));

        verify(adminService, times(1)).updateUserRole(username, role, add);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        List<UserResponse> mockUsers = List.of(
                new UserResponse("user1", "user1@example.com", Collections.singletonList(Role.USER))
        );

        when(adminService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("USER"));

        verify(adminService, times(1)).getAllUsers();
    }
}

