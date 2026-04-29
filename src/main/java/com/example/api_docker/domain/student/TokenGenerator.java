package com.example.api_docker.domain.student;

import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.domain.user.UserRole;

public interface TokenGenerator {
    String generate(UserId userId, Email email, UserRole userRole);
}
