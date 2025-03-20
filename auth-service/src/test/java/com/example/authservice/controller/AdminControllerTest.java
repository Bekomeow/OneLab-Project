//package com.example.authservice.controller;
//
//import com.example.authservice.dto.UserResponse;
//import com.example.authservice.entity.User;
//import com.example.authservice.enums.Role;
//import com.example.authservice.service.AdminService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultMatcher;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//class AdminControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AdminService adminService;
//
//    @WithMockUser(authorities = "ROLE_ADMIN")
//    @Test
//    void testGetAllUsers() throws Exception {
//        List<UserResponse> users = List.of(new UserResponse("Admin", "admin@example.com", Collections.singletonList(Role.USER)));
//
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/auth/admin/users"))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) jsonPath("$[0].username").value("testUser"))
//                .andExpect((ResultMatcher) jsonPath("$[0].role").value("USER"));
//    }
//}
