package com.example.api_docker.domain.course.exception;

public class DuplicateLessonOrderException extends RuntimeException {
    public DuplicateLessonOrderException(int order) {
    }
}
