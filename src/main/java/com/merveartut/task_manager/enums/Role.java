package com.merveartut.task_manager.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    PROJECT_MANAGER,
    TEAM_LEADER,
    TEAM_MEMBER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
