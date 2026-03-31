package com.example.api_docker.domain.student;

import java.util.Objects;
import java.util.UUID;

public record StudentId(UUID value) {
    public StudentId {
        Objects.requireNonNull(value, "StudentId não pode ser nulo");
    }

    public static StudentId generate() { return new StudentId(UUID.randomUUID()); }
    public static StudentId of(String raw) { return new StudentId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
