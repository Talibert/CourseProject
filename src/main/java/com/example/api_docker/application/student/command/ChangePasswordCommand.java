package com.example.api_docker.application.student.command;

import com.example.api_docker.domain.student.StudentId;

public record ChangePasswordCommand(StudentId studentId, String currentPassword, String newPassword) {}
