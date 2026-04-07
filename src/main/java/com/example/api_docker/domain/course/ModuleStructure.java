package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ModuleStructure{

    private final ModuleId moduleId;
    private final String title;
    private final List<LessonId> lessons;

    private ModuleStructure (ModuleId moduleId, String title, List<LessonId> lessons) {
        this.moduleId = moduleId;
        this.title = title;
        this.lessons = lessons;
    }

    public static ModuleStructure of(ModuleId moduleId, String title, List<LessonId> lessons){
        Objects.requireNonNull(moduleId, "ModuleId não pode ser nulo");

        Objects.requireNonNull(title, "Title não pode ser nulo");

        if (lessons == null || lessons.isEmpty())
            throw new DomainException("Um módulo precisa de ao menos uma lesson.");

        return new ModuleStructure(moduleId, title, lessons);
    }
}
