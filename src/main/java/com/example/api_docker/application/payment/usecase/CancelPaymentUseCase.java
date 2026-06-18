package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.application.enrollment.command.CancelEnrollmentCommand;
import com.example.api_docker.application.enrollment.usecase.CancelEnrollmentUseCase;
import com.example.api_docker.application.payment.command.CancelPaymentCommand;
import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.payment.exception.PaymentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// application/payment/CancelPaymentUseCase.java
@Component
@RequiredArgsConstructor
public class CancelPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final CancelEnrollmentUseCase cancelEnrollmentUseCase;
    private final DomainEventPublisher eventPublisher;

    public void execute(CancelPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));

        payment.cancel();
        paymentRepository.save(payment);
        payment.pullDomainEvents().forEach(eventPublisher::publish);

        cancelEnrollmentUseCase.execute(
                new CancelEnrollmentCommand(
                        payment.getEnrollmentId(),
                        CancellationReason.PAYMENT_OVERDUE
                )
        );
    }
}
