package com.example.api_docker.infra.kafka.payment;

import com.example.api_docker.application.payment.command.CancelPaymentCommand;
import com.example.api_docker.application.payment.usecase.CancelPaymentUseCase;
import com.example.api_docker.domain.payment.event.PaymentCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancelledConsumer {

    private final CancelPaymentUseCase cancelPaymentUseCase;

    @KafkaListener(topics = "payment.cancelled")
    public void handle(PaymentCancelledEvent event) {
        cancelPaymentUseCase.execute(
                new CancelPaymentCommand(event.paymentId())
        );
    }
}
