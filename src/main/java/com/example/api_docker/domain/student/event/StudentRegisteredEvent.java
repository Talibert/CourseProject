package com.example.api_docker.domain.student.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentRegisteredEvent(UUID eventId, LocalDateTime occurredAt, StudentId studentId, Email email) implements DomainEvent {

    public StudentRegisteredEvent(StudentId studentId, Email email){
        this(UUID.randomUUID(), LocalDateTime.now(), studentId, email);
    }
}
