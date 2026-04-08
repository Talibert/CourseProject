package com.example.api_docker.domain.certificate;

import java.util.Objects;
import java.util.UUID;

public record CertificateId(UUID value) {
    public CertificateId {
        Objects.requireNonNull(value, "CertificateId não pode ser nulo");
    }

    public static CertificateId generate() { return new CertificateId(UUID.randomUUID()); }
    public static CertificateId of(String raw) { return new CertificateId(UUID.fromString(raw)); }

    @Override public String toString() { return value.toString(); }
}
