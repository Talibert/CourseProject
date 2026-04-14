package com.example.api_docker.domain.student;

public class InvalidStudentTransitionException extends RuntimeException {
    public InvalidStudentTransitionException(StudentStatus actualStatus, StudentStatus newStatus) {
    }
}
