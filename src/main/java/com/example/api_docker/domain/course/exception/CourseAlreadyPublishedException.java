package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.course.CourseId;

public class CourseAlreadyPublishedException extends RuntimeException {
    public CourseAlreadyPublishedException(CourseId id) {
    }
}
