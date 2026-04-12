package com.example.api_docker.domain.enrollment.exception;

import com.example.api_docker.domain.enrollment.EnrollmentId;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(EnrollmentId enrollmentId) {
    }
}
