package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.application.payment.command.CreatePaymentCommand;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(CreatePaymentCommand command) {
        Payment payment = Payment.create(
                command.enrollmentId(),
                command.studentId(),
                command.amount(),
                command.method(),
                command.installments()
        );
        paymentRepository.save(payment);
        payment.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
