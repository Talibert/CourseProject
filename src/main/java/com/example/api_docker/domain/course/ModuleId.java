package com.example.api_docker.domain.course;

import java.util.Objects;
import java.util.UUID;

public record ModuleId(UUID value) {
    public ModuleId {
        Objects.requireNonNull(value, "ModuleId não pode ser nulo");
    }

    public static ModuleId generate() { return new ModuleId(UUID.randomUUID()); }
    public static ModuleId of(String raw) { return new ModuleId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
