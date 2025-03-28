package com.merveartut.task_manager.model;


import com.merveartut.task_manager.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    private String description;

    private ProjectStatus status;

    private String departmentName;

    @ManyToMany
    private List<User> teamMembers;

    @OneToMany
    private List<Task> tasks;

}
