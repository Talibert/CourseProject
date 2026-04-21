package com.example.api_docker.application.student.command;

import com.example.api_docker.domain.student.StudentId;

public record BanStudentCommand(StudentId studentId) {}
