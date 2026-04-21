package com.example.api_docker.application.student.command;

import java.time.LocalDate;

public record RegisterStudentCommand(String firstName, String lastName, String email,
                                     String cpf, LocalDate birthDate, String rawPassword) {}
