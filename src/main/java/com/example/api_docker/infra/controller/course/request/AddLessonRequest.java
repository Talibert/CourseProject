package com.example.api_docker.infra.controller.course.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddLessonRequest(
        @NotBlank(message = "Título não pode ser vazio")
        String title,

        @Min(value = 1, message = "Ordem deve ser maior que zero")
        int order,

        @Min(value = 1, message = "Duração deve ser maior que zero")
        int durationMinutes
) {}
