package com.example.api_docker.infra.controller.auth;

import com.example.api_docker.application.auth.LoginUseCase;
import com.example.api_docker.application.shared.LoginCommand;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.infra.controller.auth.request.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody @Valid LoginRequest request) {
        var command = new LoginCommand(request.email(), request.password());
        var result = loginUseCase.execute(command);
        return ResponseEntity.ok(result);
    }
}
