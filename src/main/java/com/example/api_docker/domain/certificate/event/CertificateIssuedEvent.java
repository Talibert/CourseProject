package com.example.api_docker.domain.certificate.event;

import com.example.api_docker.domain.certificate.CertificateId;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.StudentId;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificateIssuedEvent(UUID eventId, LocalDateTime occurredAt,
                                     CertificateId certificateId, StudentId studentId, CourseId courseId) implements DomainEvent {

    public CertificateIssuedEvent(CertificateId certificateId, StudentId studentId, CourseId courseId) {
        this(UUID.randomUUID(), LocalDateTime.now(), certificateId, studentId, courseId);
    }
}
