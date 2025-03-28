package com.merveartut.task_manager;

import com.merveartut.task_manager.config.SecurityConfig;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.security.JwtUtil;
import com.merveartut.task_manager.service.ProjectService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
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
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtUtil jwtUtil;

    private Project project;
    private UUID projectId;

    private String teamLeaderToken;
    private String teamMemberToken;
    private String projectManagerToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        project = new Project();
        projectId = UUID.randomUUID();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDescription("This is a test project.");
        project.setDepartmentName("Test");
        project.setTeamMembers(null);
        project.setTasks(null);

        teamLeaderToken = "Bearer " + jwtUtil.generateToken("teamLeaderUser", Role.TEAM_LEADER).trim();
        teamMemberToken = "Bearer " + jwtUtil.generateToken("teamMemberUser", Role.TEAM_MEMBER).trim();
        projectManagerToken = "Bearer " + jwtUtil.generateToken("projectManagerUser", Role.PROJECT_MANAGER).trim();

    }

    @Test
     void createProject_Success() throws Exception {
        when(projectService.createProject(project)).thenReturn(project);

        mockMvc.perform(post("/api/projects/v1")
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Project\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void createProject_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/projects/v1")
                        .header("Authorization", teamMemberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Project\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProjects_Success() throws Exception {
        when(projectService.getProjects()).thenReturn(List.of(project));

        mockMvc.perform(get("/api/projects/v1")
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getProjects_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/projects/v1")
                        .header("Authorization", teamMemberToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProjectById_Success() throws Exception {
        when(projectService.getProjectById(projectId)).thenReturn(project);

        mockMvc.perform(get("/api/projects/v1/{id}", projectId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }
    @Test
    public void getProjectById_ProjectNotFound() throws Exception{
        UUID invalidId = UUID.randomUUID();
        when(projectService.getProjectById(invalidId)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/api/projects/v1/" + invalidId)
                        .header("Authorization", projectManagerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectById_Unauthorized() throws Exception {
        when(projectService.getProjectById(any(UUID.class))).thenReturn(project);

        mockMvc.perform(get("/api/projects/v1/{id}", projectId)
                        .header("Authorization", teamMemberToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProject_Success() throws Exception {
        project.setTitle("Updated Project");

        when(projectService.updateProject(projectId, project)).thenReturn(project);

        mockMvc.perform(put("/api/projects/v1/{id}", projectId)
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Project\",\"description\":\"Updated Description\",\"departmentName\":\"Updated Department\"}"))
                .andExpect(status().isOk());
    }
    @Test
    public void updateProject_ProjectNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();
        when(projectService.updateProject(any(UUID.class), any(Project.class))).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(put("/api/projects/v1/"+ invalidId)
                        .header("Authorization", projectManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Project\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_Unauthorized() throws Exception {
        mockMvc.perform(put("/api/projects/v1/{id}", projectId)
                        .header("Authorization", teamMemberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Project\"}"))
                       .andExpect(status().isForbidden());
    }
}
