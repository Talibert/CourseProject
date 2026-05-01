package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentCompletedEvent(UUID eventId, LocalDateTime occurredAt,
                                       EnrollmentId enrollmentId, UserId userId, CourseId courseId) implements DomainEvent {

    public EnrollmentCompletedEvent(EnrollmentId enrollmentId, UserId userId, CourseId courseId) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId, userId, courseId);
    }
}
