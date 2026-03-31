package com.example.api_docker.domain.enrollment.exception;

import com.example.api_docker.domain.enrollment.EnrollmentStatusType;

public class InvalidEnrollmentTransitionException extends RuntimeException {
    public InvalidEnrollmentTransitionException(EnrollmentStatusType status, EnrollmentStatusType enrollmentStatusType) {
    }
}
