package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentCancelledEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId,
        CancellationReason reason
) implements DomainEvent {

    public EnrollmentCancelledEvent(EnrollmentId enrollmentId, CancellationReason reason) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId, reason);
    }
}
