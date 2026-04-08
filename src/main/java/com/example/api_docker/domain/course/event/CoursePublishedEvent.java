package com.example.api_docker.domain.course.event;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record CoursePublishedEvent(UUID eventId, LocalDateTime occurredAt, CourseId courseId) implements DomainEvent {

    public CoursePublishedEvent(CourseId courseId) {
        this(UUID.randomUUID(), LocalDateTime.now(), courseId);
    }
}
