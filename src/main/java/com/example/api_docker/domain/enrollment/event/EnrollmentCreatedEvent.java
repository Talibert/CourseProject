package com.example.api_docker.domain.enrollment.event;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        EnrollmentId enrollmentId,
        UserId studentId,
        CourseId courseId,
        BigDecimal amount,           // ← novo
        PaymentMethodType paymentMethod, // ← novo
        int installments             // ← novo
) implements DomainEvent {

    public EnrollmentCreatedEvent(EnrollmentId enrollmentId, UserId studentId,
                                  CourseId courseId, BigDecimal amount,
                                  PaymentMethodType paymentMethod, int installments) {
        this(UUID.randomUUID(), LocalDateTime.now(), enrollmentId,
                studentId, courseId, amount, paymentMethod, installments);
    }
}
