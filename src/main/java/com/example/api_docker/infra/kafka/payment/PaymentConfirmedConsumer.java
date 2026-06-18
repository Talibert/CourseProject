package com.example.api_docker.infra.kafka.payment;

import com.example.api_docker.application.payment.command.ConfirmPaymentCommand;
import com.example.api_docker.application.payment.usecase.ConfirmPaymentUseCase;
import com.example.api_docker.domain.payment.event.PaymentConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConfirmedConsumer {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @KafkaListener(topics = "payment.confirmed")
    public void handle(PaymentConfirmedEvent event) {
        confirmPaymentUseCase.execute(
                new ConfirmPaymentCommand(event.paymentId())
        );
    }
}
