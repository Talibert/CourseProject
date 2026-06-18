package com.example.api_docker.domain.payment.exception;

import com.example.api_docker.domain.payment.PaymentStatusType;

public class InvalidPaymentTransitionException extends RuntimeException {
    public InvalidPaymentTransitionException(PaymentStatusType status, PaymentStatusType paymentStatusType) {
    }
}
