package com.example.api_docker.infra.controller.payment;

import com.example.api_docker.application.payment.command.CancelPaymentCommand;
import com.example.api_docker.application.payment.command.ConfirmPaymentCommand;
import com.example.api_docker.application.payment.command.FailPaymentCommand;
import com.example.api_docker.application.payment.usecase.CancelPaymentUseCase;
import com.example.api_docker.application.payment.usecase.ConfirmPaymentUseCase;
import com.example.api_docker.application.payment.usecase.FailPaymentUseCase;
import com.example.api_docker.domain.payment.PaymentId;
import com.example.api_docker.infra.controller.payment.request.PaymentCallbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final FailPaymentUseCase failPaymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;

    @PostMapping("/callback/confirmed")
    public ResponseEntity<Void> confirmed(@RequestBody PaymentCallbackRequest request) {
        confirmPaymentUseCase.execute(
                new ConfirmPaymentCommand(new PaymentId(request.paymentId()))
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback/failed")
    public ResponseEntity<Void> failed(@RequestBody PaymentCallbackRequest request) {
        failPaymentUseCase.execute(
                new FailPaymentCommand(new PaymentId(request.paymentId()))
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback/cancelled")
    public ResponseEntity<Void> cancelled(@RequestBody PaymentCallbackRequest request) {
        cancelPaymentUseCase.execute(
                new CancelPaymentCommand(new PaymentId(request.paymentId()))
        );
        return ResponseEntity.ok().build();
    }
}
