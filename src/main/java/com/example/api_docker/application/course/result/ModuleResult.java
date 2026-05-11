package com.example.api_docker.application.course.result;

import com.example.api_docker.domain.course.Module;

import java.util.List;
import java.util.UUID;

public record ModuleResult(
        UUID moduleId,
        String title,
        int order,
        List<LessonResult> lessons
) {

    public static ModuleResult from(Module module) {
        return new ModuleResult(
                module.getId().value(),
                module.getTitle(),
                module.getOrder(),
                module.getLessons().stream()
                        .map(LessonResult::from)
                        .toList()
        );
    }
}
