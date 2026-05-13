package com.example.api_docker.domain.user;

public enum UserRole {
    STUDENT,
    ADMIN,
    INSTRUCTOR;

    public String toSecurityRole() {
        return "ROLE_" + this.name();
    }
}
