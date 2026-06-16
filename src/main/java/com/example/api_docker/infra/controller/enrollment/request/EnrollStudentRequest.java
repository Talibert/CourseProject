package com.example.api_docker.infra.controller.enrollment.request;

import com.example.api_docker.domain.payment.PaymentMethodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnrollStudentRequest(
        @NotNull(message = "CourseId não pode ser nulo")
        UUID courseId,

        @NotNull(message = "Método de pagamento não pode ser nulo")
        PaymentMethodType paymentMethod,

        @Min(value = 1, message = "Número de parcelas deve ser maior que zero")
        int installments
) {}
