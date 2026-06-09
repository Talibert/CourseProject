package com.example.api_docker.infra.controller.enrollment.request;

import com.example.api_docker.domain.enrollment.CancellationReason;
import jakarta.validation.constraints.NotNull;

public record CancelEnrollmentRequest(
        @NotNull(message = "Motivo não pode ser nulo")
        CancellationReason reason
) {}
