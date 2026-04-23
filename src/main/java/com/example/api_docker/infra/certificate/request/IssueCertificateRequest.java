package com.example.api_docker.infra.certificate.request;

import java.util.UUID;

public record IssueCertificateRequest(UUID enrollmentId) {
}
