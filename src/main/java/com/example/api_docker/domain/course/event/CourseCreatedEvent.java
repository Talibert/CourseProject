package com.example.api_docker.domain.course.event;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.InstructorId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;


public record CourseCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        CourseId courseId,
        InstructorId instructorId
) implements DomainEvent {

    public CourseCreatedEvent(CourseId courseId, InstructorId instructorId) {
        this(UUID.randomUUID(), LocalDateTime.now(), courseId, instructorId);
    }
}
