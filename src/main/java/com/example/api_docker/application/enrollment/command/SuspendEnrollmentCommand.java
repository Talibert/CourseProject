package com.example.api_docker.application.enrollment.command;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.enrollment.SuspensionReason;

public record SuspendEnrollmentCommand(EnrollmentId enrollmentId, SuspensionReason reason) {}
