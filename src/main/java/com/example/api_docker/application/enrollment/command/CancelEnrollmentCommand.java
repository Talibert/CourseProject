package com.example.api_docker.application.enrollment.command;

import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.enrollment.EnrollmentId;

public record CancelEnrollmentCommand(EnrollmentId enrollmentId, CancellationReason reason) {}

