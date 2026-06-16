package com.example.api_docker.domain.payment;

import com.example.api_docker.domain.enrollment.EnrollmentId;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findByEnrollmentId(EnrollmentId enrollmentId);
}
