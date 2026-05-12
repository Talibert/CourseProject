package com.example.api_docker.infra.controller.course.request;

import com.example.api_docker.domain.course.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank(message = "Título não pode ser vazio")
        String title,

        String description,

        @NotNull(message = "Instrutor não pode ser nulo")
        UUID instructorId,

        @NotNull(message = "Preço não pode ser nulo")
        @DecimalMin(value = "0.0", message = "Preço não pode ser negativo")
        BigDecimal price,

        @NotNull(message = "Moeda não pode ser nula")
        CurrencyType currency,

        @Min(value = 1, message = "Carga horária deve ser maior que zero")
        int estimatedHours
) {}
