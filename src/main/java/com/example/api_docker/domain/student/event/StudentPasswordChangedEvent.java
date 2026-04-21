package com.example.api_docker.domain.student.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentPasswordChangedEvent(UUID eventId, LocalDateTime occurredAt, StudentId studentId) implements DomainEvent {

    public StudentPasswordChangedEvent(StudentId studentId) {
        this(UUID.randomUUID(), LocalDateTime.now(), studentId);
    }
}
