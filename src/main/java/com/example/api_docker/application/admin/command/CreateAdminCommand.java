package com.example.api_docker.application.admin.command;

public record CreateAdminCommand(String firstName, String lastName, String email, String rawPassword) {}
