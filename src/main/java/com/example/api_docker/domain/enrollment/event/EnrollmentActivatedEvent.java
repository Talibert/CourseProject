package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentActivatedEvent(UUID eventId, LocalDateTime occurredAt, EnrollmentId enrollmentId) implements DomainEvent {

    public EnrollmentActivatedEvent(EnrollmentId enrollmentId) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId);
    }
}
