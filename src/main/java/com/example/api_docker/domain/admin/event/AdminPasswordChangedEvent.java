package com.example.api_docker.domain.admin.event;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminPasswordChangedEvent(UUID eventId, LocalDateTime occurredAt, UserId userId) implements DomainEvent {

    public AdminPasswordChangedEvent(UserId userId) {
        this(UUID.randomUUID(), LocalDateTime.now(), userId);
    }
}
