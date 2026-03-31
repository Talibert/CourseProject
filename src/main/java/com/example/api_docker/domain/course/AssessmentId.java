package com.example.api_docker.domain.course;

import java.util.Objects;
import java.util.UUID;

public record AssessmentId(UUID value) {
    public AssessmentId {
        Objects.requireNonNull(value, "AssessmentId não pode ser nulo");
    }

    public static AssessmentId generate() { return new AssessmentId(UUID.randomUUID()); }
    public static AssessmentId of(String raw) { return new AssessmentId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
