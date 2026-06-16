package com.example.api_docker.application.payment.command;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.user.UserId;

import java.math.BigDecimal;

public record CreatePaymentCommand(
        EnrollmentId enrollmentId,
        UserId studentId,
        BigDecimal amount,
        PaymentMethodType method,
        int installments
) {}
