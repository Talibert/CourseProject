package com.example.api_docker.application.admin.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminResult(UUID adminId, String fullName, String email, LocalDateTime createdAt) {}
