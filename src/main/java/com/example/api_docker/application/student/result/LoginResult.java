package com.example.api_docker.application.student.result;

import java.util.UUID;

public record LoginResult(
        String token,
        UUID userId,
        String fullName
) {}
