package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;
import lombok.Getter;

@Getter
public class Lesson {

    private final LessonId id;
    private final String title;
    private final int order;
    private final int durationMinutes;

    private Lesson(LessonId id, String title, int order, int durationMinutes) {
        if (durationMinutes <= 0)
            throw new DomainException("Duração da aula deve ser maior que zero");

        this.id = id;
        this.title = title;
        this.order = order;
        this.durationMinutes = durationMinutes;
    }

    public static Lesson create(String title, int order, int durationMinutes) {
        return new Lesson(LessonId.generate(), title, order, durationMinutes);
    }

    public static Lesson restore(LessonId id, String title, int order, int durationMinutes) {
        return new Lesson(id, title, order, durationMinutes);
    }
}
