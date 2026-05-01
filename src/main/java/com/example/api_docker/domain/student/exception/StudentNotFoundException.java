package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.user.UserId;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(UserId userId) {
    }
}
