package com.merveartut.task_manager.model;

import com.merveartut.task_manager.enums.TaskPriority;
import com.merveartut.task_manager.enums.TaskState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskPriority priority;

    @ManyToOne
    private User assignee;

    @ManyToOne
    @NotNull
    private Project project;

    @OneToMany
    private List<Comment> comments;

    private String reason;

}
