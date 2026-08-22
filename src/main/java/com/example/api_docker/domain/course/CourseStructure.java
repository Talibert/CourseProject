package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record CourseStructure(CourseId courseId, List<ModuleStructure> modules) {

    public CourseStructure {
        Objects.requireNonNull(courseId, "CourseId não pode ser nulo");

        if (modules == null || modules.isEmpty())
            throw new DomainException("Um curso precisa ter ao menos um módulo");

        modules = List.copyOf(modules);
    }

    public static CourseStructure of(CourseId courseId, List<ModuleStructure> modules) {
        return new CourseStructure(courseId, modules);
    }

    // Pergunta principal: essa aula pertence a este curso?
    public boolean contains(LessonId lessonId) {
        return modules.stream()
                .flatMap(m -> m.lessons().stream())
                .anyMatch(l -> l.equals(lessonId));
    }

    // Quantas aulas no total — usado pelo Progress para calcular %
    public int totalLessons() {
        return modules.stream()
                .mapToInt(m -> m.lessons().size())
                .sum();
    }

    // Qual o índice sequencial de uma aula — usado para desbloquear a próxima
    public Optional<LessonId> nextLesson(LessonId current) {
        List<LessonId> allLessons = modules.stream()
                .flatMap(m -> m.lessons().stream())
                .toList();

        int index = allLessons.indexOf(current);

        if (index < 0 || index >= allLessons.size() - 1)
            return Optional.empty();

        return Optional.of(allLessons.get(index + 1));
    }
}
