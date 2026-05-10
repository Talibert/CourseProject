package com.example.api_docker.application.course;

import com.example.api_docker.domain.course.Course;

import java.math.BigDecimal;
import java.util.UUID;

// application/course/result/CourseResult.java
public record CourseResult(
        UUID courseId,
        String title,
        String description,
        BigDecimal price,
        String currency,
        int estimatedHours,
        String status,
        List<ModuleResult> modules,
        AssessmentResult assessment
) {
    public static CourseResult from(Course course) {
        return new CourseResult(
                course.getId().value(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice().amount(),
                course.getPrice().currency().name(),
                course.getEstimatedHours(),
                course.getStatus().name(),
                course.getModules().stream()
                        .map(ModuleResult::from)
                        .toList(),
                course.getAssessment() != null
                        ? AssessmentResult.from(course.getAssessment())
                        : null
        );
    }
}
