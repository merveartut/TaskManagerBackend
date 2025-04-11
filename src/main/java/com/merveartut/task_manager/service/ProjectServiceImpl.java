package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.ProjectStatus;
import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.Task;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.ProjectRepository;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import com.merveartut.task_manager.service.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    @Override
    public Project createProject(Project project) {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(UUID id) throws ProjectNotFoundException{
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException());
    }

    @Override
    public Project updateProject(Project project) throws ProjectNotFoundException{
        if (!projectRepository.existsById(project.getId())) {
            throw new ProjectNotFoundException();
        }
        return projectRepository.save(project);
    }

    @Override
    public List<User> getProjectTeamMembers(UUID id) throws ProjectNotFoundException{
        Project project = projectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        return project.getTeamMembers();
    }
}
