package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.student.StudentId;

public class StudentNotActiveException extends RuntimeException {
    public StudentNotActiveException(StudentId studentId) {
    }
}
