package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.enrollment.SuspensionReason;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

// domain/enrollment/event/EnrollmentSuspendedEvent.java
public record EnrollmentSuspendedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId,
        SuspensionReason reason
) implements DomainEvent {

    public EnrollmentSuspendedEvent(EnrollmentId enrollmentId, SuspensionReason reason) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId, reason);
    }
}
