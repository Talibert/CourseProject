package com.example.api_docker.application.user.command;

public record CreateInstructorCommand(
        String firstName,
        String lastName,
        String email,
        String rawPassword,
        String bio,
        String specialty
) {}
