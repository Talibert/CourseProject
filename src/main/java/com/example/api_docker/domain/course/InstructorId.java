package com.example.api_docker.domain.course;

import java.util.Objects;
import java.util.UUID;

public record InstructorId(UUID value) {
    public InstructorId {
        Objects.requireNonNull(value, "InstructorId não pode ser nulo");
    }

    public static InstructorId generate() { return new InstructorId(UUID.randomUUID()); }
    public static InstructorId of(String raw) { return new InstructorId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
