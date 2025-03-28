package com.merveartut.task_manager;

import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.UserService;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;
    private User user;
    private UUID userId;
    private String teamLeaderToken;
    private String teamMemberToken;
    private String projectManagerToken;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        user = new User();
        userId = UUID.randomUUID();
        user.setId(userId);
        user.setName("testuser");
        user.setRole(Role.PROJECT_MANAGER);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER).trim();
    }

    @Test
    public void createUser_Success() throws Exception {
        when(userService.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/api/users/v1")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"testuser\",\"role\":\"PROJECT_MANAGER\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUsers_Success() throws Exception {
        when(userService.listUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/v1")
                .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserById_Success() throws Exception {
        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/v1/"+ userId)
                        .header("Authorization", projectManagerToken))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserById_ShouldThrowException() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(userService.getUserById(invalidId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/users/v1/{id}", invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserByRole_Success() throws Exception {
        when(userService.getUsersByRole(Role.PROJECT_MANAGER)).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/v1/role?role=PROJECT_MANAGER")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getUsersByRole_InvalidRole() throws Exception {
        mockMvc.perform(get("/api/users/v1/role?role=INVALID_ROLE")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_Success() throws Exception {
        user.setName("updateduser");
        when(userService.updateUser(userId, user)).thenReturn(user);

        mockMvc.perform(put("/api/users/v1/{id}", userId)
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updateduser\",\"role\":\"PROJECT_MANAGER\"}"))
                .andExpect(status().isOk());
    }
    @Test
    public void updateUser_NotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(userService.updateUser(any(UUID.class), any(User.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/api/users/v1/{id}", invalidId)
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"updateduser\",\"role\":\"PROJECT_MANAGER\"}"))
                .andExpect(status().isNotFound());
    }
}
