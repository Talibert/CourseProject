package com.example.api_docker.application.student.command;

import com.example.api_docker.domain.user.UserId;

public record ReactivateStudentCommand(UserId userId) {}
