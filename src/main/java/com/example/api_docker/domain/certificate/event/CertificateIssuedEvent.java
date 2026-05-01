package com.example.api_docker.domain.certificate.event;

import com.example.api_docker.domain.certificate.CertificateId;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificateIssuedEvent(UUID eventId, LocalDateTime occurredAt,
                                     CertificateId certificateId, UserId userId, CourseId courseId) implements DomainEvent {

    public CertificateIssuedEvent(CertificateId certificateId, UserId userId, CourseId courseId) {
        this(UUID.randomUUID(), LocalDateTime.now(), certificateId, userId, courseId);
    }
}
