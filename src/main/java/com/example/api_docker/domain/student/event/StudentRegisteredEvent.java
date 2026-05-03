package com.example.api_docker.domain.student.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentRegisteredEvent(UUID eventId, LocalDateTime occurredAt, UserId userId, Email email) implements DomainEvent {

    public StudentRegisteredEvent(UserId userId, Email email){
        this(UUID.randomUUID(), LocalDateTime.now(), userId, email);
    }
}
