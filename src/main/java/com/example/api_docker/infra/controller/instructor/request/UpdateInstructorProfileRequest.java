package com.example.api_docker.infra.controller.instructor.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateInstructorProfileRequest(
        @NotBlank(message = "Bio não pode ser vazia")
        String bio,

        @NotBlank(message = "Especialidade não pode ser vazia")
        String specialty,

        String linkedin,
        String github,
        String youtube,
        String instagram
) {}
