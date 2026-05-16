package com.example.api_docker.infra.controller.instructor;

import com.example.api_docker.application.user.command.CreateInstructorCommand;
import com.example.api_docker.application.user.command.UpdateInstructorProfileCommand;
import com.example.api_docker.application.user.query.GetInstructorQuery;
import com.example.api_docker.application.user.result.InstructorResult;
import com.example.api_docker.application.user.usecase.CreateInstructorUseCase;
import com.example.api_docker.application.user.usecase.GetInstructorUseCase;
import com.example.api_docker.application.user.usecase.UpdateInstructorProfileUseCase;
import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.infra.controller.instructor.request.CreateInstructorRequest;
import com.example.api_docker.infra.controller.instructor.request.UpdateInstructorProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private final CreateInstructorUseCase createInstructorUseCase;
    private final GetInstructorUseCase getInstructorUseCase;
    private final UpdateInstructorProfileUseCase updateInstructorProfileUseCase;

    @PostMapping("/register")
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateInstructorRequest request) {
        CreateInstructorCommand command = new CreateInstructorCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.bio(),
                request.specialty()
        );
        createInstructorUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<InstructorResult> me(
            @AuthenticationPrincipal UserId instructorId) {
        var result = getInstructorUseCase.execute(new GetInstructorQuery(instructorId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResult> findById(@PathVariable UUID id) {
        var result = getInstructorUseCase.execute(new GetInstructorQuery(new UserId(id)));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal UserId instructorId,
            @RequestBody @Valid UpdateInstructorProfileRequest request) {
        UpdateInstructorProfileCommand command = new UpdateInstructorProfileCommand(
                instructorId,
                request.bio(),
                request.specialty(),
                SocialLinks.of(
                        request.linkedin(),
                        request.github(),
                        request.youtube(),
                        request.instagram()
                )
        );
        updateInstructorProfileUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}
