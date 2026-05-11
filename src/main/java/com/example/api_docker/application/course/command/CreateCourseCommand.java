package com.example.api_docker.application.course.command;

import com.example.api_docker.domain.course.CurrencyType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCourseCommand(
        String title,
        String description,
        UUID instructorId,
        BigDecimal price,
        CurrencyType currency,
        int estimatedHours
) {}
