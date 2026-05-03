package com.example.api_docker.domain.admin.exception;

import com.example.api_docker.domain.user.UserId;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(UserId userId) {
    }
}
