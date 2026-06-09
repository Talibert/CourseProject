package com.example.api_docker.infra.controller.enrollment.request;

import com.example.api_docker.domain.enrollment.SuspensionReason;
import jakarta.validation.constraints.NotNull;

public record SuspendEnrollmentRequest(
        @NotNull(message = "Motivo não pode ser nulo")
        SuspensionReason reason
) {}
