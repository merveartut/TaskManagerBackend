package com.merveartut.task_manager.controller;

import com.merveartut.task_manager.model.Project;
import com.merveartut.task_manager.service.ProjectService;
import com.merveartut.task_manager.service.exception.ProjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/v1")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping("/v1")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(projectService.getProjects());
    }

    @GetMapping("/v1/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }



    @PutMapping("/v1/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException ex) {
        return ResponseEntity.status(404).body("Project not found");
    }
}
