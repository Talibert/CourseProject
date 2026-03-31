package com.example.api_docker.domain.course;

import java.util.Objects;
import java.util.UUID;

public record LessonId(UUID value) {
    public LessonId {
        Objects.requireNonNull(value, "LessonsId não pode ser nulo");
    }

    public static LessonId generate() { return new LessonId(UUID.randomUUID()); }
    public static LessonId of(String raw) { return new LessonId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
