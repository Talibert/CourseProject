package com.example.api_docker.domain.course;

import java.util.Objects;
import java.util.UUID;

public record CourseId(UUID value) {
    public CourseId {
        Objects.requireNonNull(value, "CourseId não pode ser nulo");
    }

    public static CourseId generate() { return new CourseId(UUID.randomUUID()); }
    public static CourseId of(String raw) { return new CourseId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
