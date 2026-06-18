package com.example.api_docker.application.payment.command;

import com.example.api_docker.domain.payment.PaymentId;

// application/payment/command/ConfirmPaymentCommand.java
public record ConfirmPaymentCommand(PaymentId paymentId) {}
