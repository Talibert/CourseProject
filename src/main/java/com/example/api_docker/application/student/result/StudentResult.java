package com.example.api_docker.application.student.result;

import com.example.api_docker.domain.student.StudentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResult(
        UUID userId,
        String fullName,
        String email,
        StudentStatus status,
        LocalDateTime createdAt
) {}
