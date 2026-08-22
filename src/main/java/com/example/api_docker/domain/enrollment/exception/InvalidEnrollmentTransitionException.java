package com.example.api_docker.domain.enrollment.exception;

import com.example.api_docker.domain.enrollment.EnrollmentStatusType;

public class InvalidEnrollmentTransitionException extends RuntimeException {
    public InvalidEnrollmentTransitionException(EnrollmentStatusType currentStatus, EnrollmentStatusType targetStatus) {
        super("Transição de status da matrícula inválida: de %s para %s".formatted(currentStatus, targetStatus));
    }
}
