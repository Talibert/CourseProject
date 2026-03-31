package com.example.api_docker.domain.enrollment.exception;

import com.example.api_docker.domain.enrollment.EnrollmentId;

public class EnrollmentNotActiveException extends RuntimeException {
    public EnrollmentNotActiveException(EnrollmentId id) {
    }
}
