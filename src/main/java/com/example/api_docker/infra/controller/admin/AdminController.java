package com.example.api_docker.infra.controller.admin;

import com.example.api_docker.application.admin.command.CreateAdminCommand;
import com.example.api_docker.application.admin.query.GetAdminQuery;
import com.example.api_docker.application.admin.result.AdminResult;
import com.example.api_docker.application.admin.usecase.CreateAdminUseCase;
import com.example.api_docker.application.admin.usecase.GetAdminUseCase;
import com.example.api_docker.application.admin.usecase.LoginAdminUseCase;
import com.example.api_docker.application.shared.LoginCommand;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.infra.controller.admin.request.CreateAdminRequest;
import com.example.api_docker.infra.controller.admin.request.LoginRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final CreateAdminUseCase createAdminUseCase;
    private final LoginAdminUseCase loginAdminUseCase;
    private final GetAdminUseCase getAdminUseCase;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid CreateAdminRequest request) {
        CreateAdminCommand command = new CreateAdminCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
        createAdminUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody @Valid LoginRequest request) {
        var command = new LoginCommand(request.email(), request.password());
        var result = loginAdminUseCase.execute(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResult> me(@AuthenticationPrincipal UserId adminId) {
        var result = getAdminUseCase.execute(new GetAdminQuery(adminId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResult> findById(@PathVariable UUID id) {
        var result = getAdminUseCase.execute(new GetAdminQuery(new UserId(id)));
        return ResponseEntity.ok(result);
    }
}
