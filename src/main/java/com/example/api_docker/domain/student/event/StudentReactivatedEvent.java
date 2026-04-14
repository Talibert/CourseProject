package com.example.api_docker.domain.student.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentReactivatedEvent(UUID eventId, LocalDateTime occurredAt, StudentId studentId) implements DomainEvent {

    public StudentReactivatedEvent(StudentId studentId){
        this(UUID.randomUUID(), LocalDateTime.now(), studentId);
    }
}
