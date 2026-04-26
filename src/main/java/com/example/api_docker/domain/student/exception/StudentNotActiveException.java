package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.user.UserId;

public class StudentNotActiveException extends RuntimeException {
    public StudentNotActiveException(UserId userId) {
    }
}
