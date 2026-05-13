package com.example.api_docker.domain.instructor.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;


public record InstructorProfileUpdatedEvent(UUID eventId, LocalDateTime occurredAt,
                                            UserId userId) implements DomainEvent {

    public InstructorProfileUpdatedEvent(UserId userId) {
        this(UUID.randomUUID(), LocalDateTime.now(), userId);
    }
}
