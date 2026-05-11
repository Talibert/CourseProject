package com.example.api_docker.application.course.result;

import com.example.api_docker.domain.course.Assessment;

import java.math.BigDecimal;
import java.util.UUID;

public record AssessmentResult(
        UUID assessmentId,
        String title,
        BigDecimal minimumGrade,
        BigDecimal maximumGrade
) {
    public static AssessmentResult from(Assessment assessment) {
        return new AssessmentResult(
                assessment.getId().value(),
                assessment.getTitle(),
                assessment.getMinimumGrade(),
                assessment.getMaximumGrade()
        );
    }
}
