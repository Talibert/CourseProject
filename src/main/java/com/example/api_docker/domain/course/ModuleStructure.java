package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;

import java.util.List;
import java.util.Objects;

public record ModuleStructure(ModuleId moduleId, String title, List<LessonId> lessons) {

    public ModuleStructure {
        Objects.requireNonNull(moduleId, "ModuleId não pode ser nulo");
        Objects.requireNonNull(title, "Title não pode ser nulo");

        if (lessons == null || lessons.isEmpty())
            throw new DomainException("Um módulo precisa de ao menos uma lesson.");

        lessons = List.copyOf(lessons);
    }

    public static ModuleStructure of(ModuleId moduleId, String title, List<LessonId> lessons) {
        return new ModuleStructure(moduleId, title, lessons);
    }
}
