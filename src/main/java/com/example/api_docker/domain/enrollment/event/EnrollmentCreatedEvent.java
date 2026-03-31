package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId,
        StudentId studentId,
        CourseId courseId
) implements DomainEvent {

    public EnrollmentCreatedEvent(EnrollmentId enrollmentId, StudentId studentId, CourseId courseId) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId, studentId, courseId);
    }
}
