package com.example.api_docker.domain.user;

public enum UserRole {
    STUDENT,
    ADMIN;

    public String toSecurityRole() {
        return "ROLE_" + this.name();
    }
}
