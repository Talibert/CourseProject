package com.example.api_docker.application.enrollment.result;

import com.example.api_docker.domain.enrollment.Enrollment;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentResult(
        UUID enrollmentId,
        UUID studentId,
        UUID courseId,
        String status,
        double progressPercentage,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt
) {
    public static EnrollmentResult from(Enrollment enrollment) {
        return new EnrollmentResult(
                enrollment.getId().value(),
                enrollment.getUserId().value(),
                enrollment.getCourseId().value(),
                enrollment.getStatus().name(),
                enrollment.getProgress().percentage(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt()
        );
    }
}
