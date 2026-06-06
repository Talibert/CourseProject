package com.example.api_docker.application.enrollment.exception;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.exception.DomainException;

public class CourseNotPublishedException extends DomainException {
    public CourseNotPublishedException(CourseId id) {
        super("Curso não está publicado: " + id.value());
    }
}
