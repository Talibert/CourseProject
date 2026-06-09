package com.example.api_docker.infra.controller.enrollment.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnrollStudentRequest(
        @NotNull(message = "CourseId não pode ser nulo")
        UUID courseId
) {}
