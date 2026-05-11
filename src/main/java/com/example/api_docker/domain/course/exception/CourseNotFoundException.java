package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.exception.NotFoundException;

public class CourseNotFoundException extends NotFoundException {
    public CourseNotFoundException(CourseId id) {
        super("Curso não encontrado: " + id.value());
    }
}
