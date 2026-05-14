package com.example.api_docker.infra.controller.admin;

import com.example.api_docker.application.admin.command.CreateAdminCommand;
import com.example.api_docker.application.admin.query.GetAdminQuery;
import com.example.api_docker.application.admin.result.AdminResult;
import com.example.api_docker.application.admin.usecase.CreateAdminUseCase;
import com.example.api_docker.application.admin.usecase.GetAdminUseCase;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.infra.controller.admin.request.CreateAdminRequest;
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
