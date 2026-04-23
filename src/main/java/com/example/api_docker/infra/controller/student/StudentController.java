package com.example.api_docker.infra.controller.student;

import com.example.api_docker.application.student.command.LoginCommand;
import com.example.api_docker.application.student.command.RegisterStudentCommand;
import com.example.api_docker.application.student.query.GetStudentQuery;
import com.example.api_docker.application.student.result.LoginResult;
import com.example.api_docker.application.student.result.StudentResult;
import com.example.api_docker.application.student.usecase.GetStudentUseCase;
import com.example.api_docker.application.student.usecase.LoginStudentUseCase;
import com.example.api_docker.application.student.usecase.RegisterStudentUseCase;
import com.example.api_docker.domain.student.StudentId;
import com.example.api_docker.infra.controller.student.request.LoginRequest;
import com.example.api_docker.infra.controller.student.request.RegisterStudentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final RegisterStudentUseCase registerStudentUseCase;
    private final LoginStudentUseCase loginStudentUseCase;
    private final GetStudentUseCase getStudentUseCase;

    public StudentController(RegisterStudentUseCase registerStudentUseCase,
                             LoginStudentUseCase loginStudentUseCase,
                             GetStudentUseCase getStudentUseCase) {
        this.registerStudentUseCase = registerStudentUseCase;
        this.loginStudentUseCase = loginStudentUseCase;
        this.getStudentUseCase = getStudentUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterStudentRequest request) {
        var command = new RegisterStudentCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.cpf(),
                request.birthDate(),
                request.password()
        );
        registerStudentUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody @Valid LoginRequest request) {
        var command = new LoginCommand(request.email(), request.password());
        var result = loginStudentUseCase.execute(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<StudentResult> me(@AuthenticationPrincipal StudentId studentId) {
        var result = getStudentUseCase.execute(new GetStudentQuery(studentId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResult> findById(@PathVariable UUID id) {
        var result = getStudentUseCase.execute(new GetStudentQuery(new StudentId(id)));
        return ResponseEntity.ok(result);
    }
}
