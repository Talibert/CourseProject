package com.example.api_docker.application.enrollment.exception;

import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.exception.NotFoundException;

public class EnrollmentNotFoundException extends NotFoundException {
    public EnrollmentNotFoundException(EnrollmentId id) {
        super("Matrícula não encontrada: " + id.value());
    }
}
