package com.example.api_docker.infra.controller.course.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DefineAssessmentRequest(
        @NotBlank(message = "Título não pode ser vazio")
        String title,

        @NotNull(message = "Nota mínima não pode ser nula")
        @DecimalMin(value = "0.0", message = "Nota mínima não pode ser negativa")
        BigDecimal minimumGrade,

        @NotNull(message = "Nota máxima não pode ser nula")
        BigDecimal maximumGrade
) {}
