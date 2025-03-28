package com.merveartut.task_manager.model;

import com.merveartut.task_manager.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String password;

    @Enumerated(EnumType.STRING) // TEAM_MEMBER, TEAM_LEADER, PROJECT_MANAGER
    private Role role;


    @OneToMany
    public List<Task> tasks;
}
