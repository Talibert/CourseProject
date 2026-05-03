package com.example.api_docker.infra.controller.admin.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(
        @NotBlank(message = "Nome não pode ser vazio")
        String firstName,

        @NotBlank(message = "Sobrenome não pode ser vazio")
        String lastName,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password
) {}
