package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.shared.exception.NotFoundException;
import com.example.api_docker.domain.user.UserId;

public class InstructorNotFoundException extends NotFoundException {
    public InstructorNotFoundException(UserId id) {
        super("Instrutor não encontrado: " + id.value());
    }
}
