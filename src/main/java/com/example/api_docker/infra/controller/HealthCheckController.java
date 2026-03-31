package com.example.api_docker.infra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Endpoints de verificação do sistema")
public class HealthCheckController {

    public record HealthResponse(String status, String message) {}

    @Operation(summary = "Verifica se a API está online", description = "Retorna o status atual da aplicação e do ambiente Docker")
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        var response = new HealthResponse(
                "UP",
                "Aplicação rodando com Java 21 e Docker"
        );

        return ResponseEntity
                .ok()
                .body(response);
    }
}
