package com.example.api_docker.application.course.command;

import com.example.api_docker.domain.course.CourseId;

import java.math.BigDecimal;

public record DefineAssessmentCommand(
        CourseId courseId,
        String title,
        BigDecimal minimumGrade,
        BigDecimal maximumGrade
) {}
