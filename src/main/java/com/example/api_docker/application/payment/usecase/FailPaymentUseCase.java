package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.application.enrollment.command.CancelEnrollmentCommand;
import com.example.api_docker.application.enrollment.usecase.CancelEnrollmentUseCase;
import com.example.api_docker.application.payment.command.FailPaymentCommand;
import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.payment.exception.PaymentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final CancelEnrollmentUseCase cancelEnrollmentUseCase;
    private final DomainEventPublisher eventPublisher;

    public void execute(FailPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));

        payment.fail();
        paymentRepository.save(payment);
        payment.pullDomainEvents().forEach(eventPublisher::publish);

        // Cancela a matrícula automaticamente
        cancelEnrollmentUseCase.execute(
                new CancelEnrollmentCommand(
                        payment.getEnrollmentId(),
                        CancellationReason.PAYMENT_OVERDUE
                )
        );
    }
}
