package com.example.api_docker.infra.kafka.payment;

import com.example.api_docker.application.payment.command.FailPaymentCommand;
import com.example.api_docker.application.payment.usecase.FailPaymentUseCase;
import com.example.api_docker.domain.payment.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final FailPaymentUseCase failPaymentUseCase;

    @KafkaListener(topics = "payment.failed")
    public void handle(PaymentFailedEvent event) {
        failPaymentUseCase.execute(
                new FailPaymentCommand(event.paymentId())
        );
    }
}
