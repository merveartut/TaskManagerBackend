package com.merveartut.task_manager;

import com.merveartut.task_manager.enums.ProjectStatus;
import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.service.ProjectServiceImpl;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    private Project project;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectId = UUID.randomUUID();
        project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .description("This is a test project.")
                .departmentName("Test")
                .teamMembers(null)
                .tasks(null)
                .build();
    }

    @Test
    void testCreateProject() {
        when(projectRepository.save(project)).thenReturn(project);

        Project createdProject = projectService.createProject(project);

        assertNotNull(createdProject);
        assertEquals(project.getId(), createdProject.getId());
        assertEquals(project.getTitle(), createdProject.getTitle());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testCreateProject_SetStatusToInProgress() {
        when(projectRepository.save(project)).thenReturn(project);

        Project createdProject = projectService.createProject(project);

        assertNotNull(createdProject);
        assertEquals(ProjectStatus.IN_PROGRESS, createdProject.getStatus());
        verify(projectRepository, times(1)).save(project);
    }
    @Test
    void testListProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> projects = projectService.getProjects();

        assertEquals(1, projects.size());
        assertEquals(project, projects.get(0));
        assertEquals(project.getId(), projects.get(0).getId());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectById_Success() throws UserNotFoundException {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project foundProject = projectService.getProjectById(projectId);

        assertNotNull(foundProject);
        assertEquals(projectId, foundProject.getId());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetProjectById_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testUpdateProject_Success() throws UserNotFoundException {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.save(project)).thenReturn(project);

        Project updatedProject = projectService.updateProject(projectId, project);

        assertNotNull(updatedProject);
        assertEquals(project.getId(), updatedProject.getId());
        verify(projectRepository, times(1)).existsById(projectId);
        verify(projectRepository, times(1)).save(project);
    }
    @Test
    void testUpdateProject_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(projectId, project));
        verify(projectRepository, times(1)).existsById(projectId);
    }

}
