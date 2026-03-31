package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentReactivatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId
) implements DomainEvent {

    public EnrollmentReactivatedEvent(EnrollmentId enrollmentId) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId);
    }
}
