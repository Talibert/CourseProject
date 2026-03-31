package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgressMilestoneReachedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId,
        double milestone
) implements DomainEvent {

    public ProgressMilestoneReachedEvent(EnrollmentId enrollmentId, double milestone) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId, milestone);
    }
}
