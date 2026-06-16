package com.example.api_docker.application.payment.command;

import com.example.api_docker.domain.payment.PaymentId;

public record FailPaymentCommand(PaymentId paymentId) {}
