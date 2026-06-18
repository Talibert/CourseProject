package com.example.api_docker.infra.kafka.enrollment;

import com.example.api_docker.application.payment.command.CreatePaymentCommand;
import com.example.api_docker.application.payment.usecase.CreatePaymentUseCase;
import com.example.api_docker.domain.enrollment.event.EnrollmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentCreatedConsumer {

    private final CreatePaymentUseCase createPaymentUseCase;

    @KafkaListener(topics = "enrollment.created")
    public void handle(EnrollmentCreatedEvent event) {
        createPaymentUseCase.execute(new CreatePaymentCommand(
                event.enrollmentId(),
                event.studentId(),
                event.amount(),
                event.paymentMethod(),
                event.installments()
        ));
    }
}
