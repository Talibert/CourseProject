package com.example.api_docker.application.enrollment.command;

import com.example.api_docker.domain.enrollment.EnrollmentId;

public record ReactivateEnrollmentCommand(EnrollmentId enrollmentId) {}
