package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;

public class Lesson {

    private final LessonId id;
    private final String title;
    private final int order;
    private final int durationMinutes;

    public Lesson(String title, int order, int durationMinutes) {
        if (durationMinutes <= 0)
            throw new DomainException("Duração da aula deve ser maior que zero");

        this.id = LessonId.generate();
        this.title = title;
        this.order = order;
        this.durationMinutes = durationMinutes;
    }

    public LessonId getId()         { return id; }
    public String getTitle()        { return title; }
    public int getOrder()           { return order; }
    public int getDurationMinutes() { return durationMinutes; }
}
