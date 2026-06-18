package com.example.api_docker.domain.payment;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.event.PaymentCancelledEvent;
import com.example.api_docker.domain.payment.event.PaymentConfirmedEvent;
import com.example.api_docker.domain.payment.event.PaymentCreatedEvent;
import com.example.api_docker.domain.payment.event.PaymentFailedEvent;
import com.example.api_docker.domain.payment.exception.InvalidPaymentTransitionException;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.user.UserId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Payment {

    private final PaymentId id;
    private final EnrollmentId enrollmentId;
    private final UserId studentId;
    private final BigDecimal amount;
    private final PaymentMethodType method;
    private final int installments;
    private PaymentStatusType status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Payment(PaymentId id, EnrollmentId enrollmentId, UserId studentId,
                    BigDecimal amount, PaymentMethodType method, int installments) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.amount = amount;
        this.method = method;
        this.installments = installments;
        this.status = PaymentStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private Payment(PaymentId id, EnrollmentId enrollmentId, UserId studentId,
                    BigDecimal amount, PaymentMethodType method, int installments,
                    PaymentStatusType status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.amount = amount;
        this.method = method;
        this.installments = installments;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment create(EnrollmentId enrollmentId, UserId studentId,
                                 BigDecimal amount, PaymentMethodType method, int installments) {
        if (method == PaymentMethodType.PIX && installments > 1)
            throw new DomainException("PIX não suporta parcelamento");

        if (installments < 1)
            throw new DomainException("Número de parcelas deve ser maior que zero");

        Payment payment = new Payment(
                PaymentId.generate(), enrollmentId, studentId,
                amount, method, installments
        );
        payment.addDomainEvent(new PaymentCreatedEvent(payment.id, enrollmentId));
        return payment;
    }

    public static Payment restore(PaymentId id, EnrollmentId enrollmentId,
                                  UserId studentId, BigDecimal amount,
                                  PaymentMethodType method, int installments,
                                  PaymentStatusType status, LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
        return new Payment(id, enrollmentId, studentId, amount, method,
                installments, status, createdAt, updatedAt);
    }

    public void confirm() {
        if (status != PaymentStatusType.PENDING && status != PaymentStatusType.PROCESSING)
            throw new InvalidPaymentTransitionException(status, PaymentStatusType.CONFIRMED);

        this.status = PaymentStatusType.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new PaymentConfirmedEvent(id, enrollmentId));
    }

    public void fail() {
        if (status != PaymentStatusType.PENDING && status != PaymentStatusType.PROCESSING)
            throw new InvalidPaymentTransitionException(status, PaymentStatusType.FAILED);

        this.status = PaymentStatusType.FAILED;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new PaymentFailedEvent(id, enrollmentId));
    }

    public void cancel() {
        if (status == PaymentStatusType.CONFIRMED || status == PaymentStatusType.REFUNDED)
            throw new InvalidPaymentTransitionException(status, PaymentStatusType.CANCELLED);

        this.status = PaymentStatusType.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new PaymentCancelledEvent(id, enrollmentId));
    }

    private void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}