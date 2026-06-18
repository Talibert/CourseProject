package com.example.api_docker.domain.payment.exception;

import com.example.api_docker.domain.payment.PaymentId;
import com.example.api_docker.domain.shared.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(PaymentId paymentId) {
        super("Pagamento não encontrado: " + paymentId.value());
    }
}
