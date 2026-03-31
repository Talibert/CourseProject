package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;

import java.util.List;
import java.util.Objects;

// domain/course/ModuleStructure.java
public record ModuleStructure(ModuleId moduleId, String title, List<LessonId> lessons) {

    public ModuleStructure {
        Objects.requireNonNull(moduleId, "ModuleId não pode ser nulo");
        if (lessons == null || lessons.isEmpty()) {
            throw new DomainException("Um módulo precisa ter ao menos uma aula");
        }
        lessons = List.copyOf(lessons);
    }
}
