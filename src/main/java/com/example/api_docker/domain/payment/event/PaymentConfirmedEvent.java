package com.example.api_docker.domain.payment.event;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.PaymentId;
import com.example.api_docker.domain.shared.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentConfirmedEvent(UUID eventId, LocalDateTime occurredAt, PaymentId paymentId, EnrollmentId enrollmentId) implements DomainEvent {

    public PaymentConfirmedEvent(PaymentId paymentId, EnrollmentId enrollmentId) {
        this(UUID.randomUUID(), LocalDateTime.now(), paymentId, enrollmentId);
    }
}
