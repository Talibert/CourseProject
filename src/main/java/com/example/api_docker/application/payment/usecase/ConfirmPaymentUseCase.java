package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.application.enrollment.command.ActivateEnrollmentCommand;
import com.example.api_docker.application.enrollment.usecase.ActivateEnrollmentUseCase;
import com.example.api_docker.application.payment.command.ConfirmPaymentCommand;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.payment.exception.PaymentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final ActivateEnrollmentUseCase activateEnrollmentUseCase;
    private final DomainEventPublisher eventPublisher;

    public void execute(ConfirmPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException(command.paymentId()));

        payment.confirm();
        paymentRepository.save(payment);
        payment.pullDomainEvents().forEach(eventPublisher::publish);

        // Ativa a matrícula automaticamente
        activateEnrollmentUseCase.execute(
                new ActivateEnrollmentCommand(payment.getEnrollmentId())
        );
    }
}
