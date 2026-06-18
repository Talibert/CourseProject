package com.example.api_docker.infra.kafka.payment;

import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.payment.PaymentStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class PaymentJpaEntity {

    @Id
    @Column(name = "payment_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "enrollment_id", nullable = false, columnDefinition = "uuid")
    private UUID enrollmentId;

    @Column(name = "student_id", nullable = false, columnDefinition = "uuid")
    private UUID studentId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethodType method;

    @Column(name = "installments", nullable = false)
    private int installments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatusType status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PaymentJpaEntity() {}
}
