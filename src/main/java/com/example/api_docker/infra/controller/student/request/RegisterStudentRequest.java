package com.example.api_docker.infra.controller.student.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterStudentRequest(
        @NotBlank(message = "Nome não pode ser vazio")
        String firstName,

        @NotBlank(message = "Sobrenome não pode ser vazio")
        String lastName,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "CPF não pode ser vazio")
        String cpf,

        @NotNull(message = "Data de nascimento não pode ser nula")
        LocalDate birthDate,

        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password
) {}
