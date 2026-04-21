package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.student.StudentStatus;

public class InvalidStudentTransitionException extends RuntimeException {
    public InvalidStudentTransitionException(StudentStatus actualStatus, StudentStatus newStatus) {
    }
}
