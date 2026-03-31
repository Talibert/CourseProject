package com.example.api_docker.domain.enrollment;

import java.util.Objects;
import java.util.UUID;

public record EnrollmentId(UUID value) {
    public EnrollmentId {
        Objects.requireNonNull(value, "EnrollmentId não pode ser nulo");
    }

    public static EnrollmentId generate() { return new EnrollmentId(UUID.randomUUID()); }
    public static EnrollmentId of(String raw) { return new EnrollmentId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
