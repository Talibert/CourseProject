package com.example.api_docker.infra.controller.request;

import java.util.UUID;

public record IssueCertificateRequest(UUID enrollmentId) {
}
