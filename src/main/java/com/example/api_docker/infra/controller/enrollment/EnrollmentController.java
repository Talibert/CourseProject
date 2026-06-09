package com.example.api_docker.infra.controller.enrollment;

import com.example.api_docker.application.enrollment.command.*;
import com.example.api_docker.application.enrollment.query.GetEnrollmentQuery;
import com.example.api_docker.application.enrollment.query.ListStudentEnrollmentsQuery;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.application.enrollment.usecase.*;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.LessonId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.infra.controller.enrollment.request.CancelEnrollmentRequest;
import com.example.api_docker.infra.controller.enrollment.request.EnrollStudentRequest;
import com.example.api_docker.infra.controller.enrollment.request.SuspendEnrollmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollStudentUseCase enrollStudentUseCase;
    private final ActivateEnrollmentUseCase activateEnrollmentUseCase;
    private final SuspendEnrollmentUseCase suspendEnrollmentUseCase;
    private final ReactivateEnrollmentUseCase reactivateEnrollmentUseCase;
    private final CancelEnrollmentUseCase cancelEnrollmentUseCase;
    private final WatchLessonUseCase watchLessonUseCase;
    private final CompleteEnrollmentUseCase completeEnrollmentUseCase;
    private final GetEnrollmentUseCase getEnrollmentUseCase;
    private final ListStudentEnrollmentsUseCase listStudentEnrollmentsUseCase;

    @PostMapping
    public ResponseEntity<EnrollmentResult> enroll(
            @AuthenticationPrincipal UserId studentId,
            @RequestBody @Valid EnrollStudentRequest request) {
        EnrollmentResult result = enrollStudentUseCase.execute(
                new EnrollStudentCommand(studentId, new CourseId(request.courseId()))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{enrollmentId}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID enrollmentId) {
        activateEnrollmentUseCase.execute(
                new ActivateEnrollmentCommand(new EnrollmentId(enrollmentId))
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{enrollmentId}/suspend")
    public ResponseEntity<Void> suspend(
            @PathVariable UUID enrollmentId,
            @RequestBody @Valid SuspendEnrollmentRequest request) {
        suspendEnrollmentUseCase.execute(
                new SuspendEnrollmentCommand(
                        new EnrollmentId(enrollmentId),
                        request.reason()
                )
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{enrollmentId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID enrollmentId) {
        reactivateEnrollmentUseCase.execute(
                new ReactivateEnrollmentCommand(new EnrollmentId(enrollmentId))
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{enrollmentId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID enrollmentId,
            @RequestBody @Valid CancelEnrollmentRequest request) {
        cancelEnrollmentUseCase.execute(
                new CancelEnrollmentCommand(
                        new EnrollmentId(enrollmentId),
                        request.reason()
                )
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{enrollmentId}/lessons/{lessonId}/watch")
    public ResponseEntity<Void> watchLesson(
            @PathVariable UUID enrollmentId,
            @PathVariable UUID lessonId) {
        watchLessonUseCase.execute(
                new WatchLessonCommand(
                        new EnrollmentId(enrollmentId),
                        new LessonId(lessonId)
                )
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{enrollmentId}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID enrollmentId) {
        completeEnrollmentUseCase.execute(
                new CompleteEnrollmentCommand(new EnrollmentId(enrollmentId))
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResult> findById(@PathVariable UUID enrollmentId) {
        EnrollmentResult result = getEnrollmentUseCase.execute(
                new GetEnrollmentQuery(new EnrollmentId(enrollmentId))
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResult>> listMyEnrollments(
            @AuthenticationPrincipal UserId studentId) {
        List<EnrollmentResult> results = listStudentEnrollmentsUseCase.execute(
                new ListStudentEnrollmentsQuery(studentId)
        );
        return ResponseEntity.ok(results);
    }
}
