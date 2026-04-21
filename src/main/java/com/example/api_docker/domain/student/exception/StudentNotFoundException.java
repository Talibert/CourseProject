package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.student.StudentId;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(StudentId studentId) {
    }
}
