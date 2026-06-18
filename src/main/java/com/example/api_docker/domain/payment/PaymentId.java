package com.example.api_docker.domain.payment;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
    public PaymentId {
        Objects.requireNonNull(value, "PaymentId não pode ser nulo");
    }

    public static PaymentId generate() { return new PaymentId(UUID.randomUUID()); }
    public static PaymentId of(String raw) { return new PaymentId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
