package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.course.CourseId;

public class CoursePublishNotAllowedException extends RuntimeException {
    public CoursePublishNotAllowedException(String error) {
    }
}
