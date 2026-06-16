package com.example.api_docker.infra.controller.payment.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCallbackRequest(
        @NotNull(message = "PaymentId não pode ser nulo")
        UUID paymentId
) {}
