package com.example.api_docker.domain.student;

public interface TokenGenerator {
    String generate(StudentId studentId, Email email);
}
