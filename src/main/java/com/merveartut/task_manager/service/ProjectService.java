package com.merveartut.task_manager.service;

import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project createProject(Project project);
    List<Project> getProjects();
    Project getProjectById(UUID id) throws ProjectNotFoundException;
    Project updateProject(Project project) throws ProjectNotFoundException;
    List<User> getProjectTeamMembers(UUID id) throws ProjectNotFoundException;

}
