package com.example.api_docker.infra.kafka.payment;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentId;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public void save(Payment payment) {
        jpaRepository.save(toJpaEntity(payment));
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByEnrollmentId(EnrollmentId enrollmentId) {
        return jpaRepository.findByEnrollmentId(enrollmentId.value()).map(this::toDomain);
    }

    private PaymentJpaEntity toJpaEntity(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setId(payment.getId().value());
        entity.setEnrollmentId(payment.getEnrollmentId().value());
        entity.setStudentId(payment.getStudentId().value());
        entity.setAmount(payment.getAmount());
        entity.setMethod(payment.getMethod());
        entity.setInstallments(payment.getInstallments());
        entity.setStatus(payment.getStatus());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setUpdatedAt(payment.getUpdatedAt());
        return entity;
    }

    private Payment toDomain(PaymentJpaEntity entity) {
        return Payment.restore(
                new PaymentId(entity.getId()),
                new EnrollmentId(entity.getEnrollmentId()),
                new UserId(entity.getStudentId()),
                entity.getAmount(),
                entity.getMethod(),
                entity.getInstallments(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
