package com.example.api_docker.domain.course.exception;

import com.example.api_docker.domain.course.ModuleId;
import com.example.api_docker.domain.shared.exception.NotFoundException;

public class ModuleNotFoundException extends NotFoundException {
    public ModuleNotFoundException(ModuleId id) {
        super("Módulo não encontrado: " + id.value());
    }
}
