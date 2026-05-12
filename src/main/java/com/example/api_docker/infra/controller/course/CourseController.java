package com.example.api_docker.infra.controller.course;

import com.example.api_docker.application.course.command.*;
import com.example.api_docker.application.course.query.GetCourseQuery;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.application.course.usecase.*;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.ModuleId;
import com.example.api_docker.infra.controller.course.request.AddLessonRequest;
import com.example.api_docker.infra.controller.course.request.AddModuleRequest;
import com.example.api_docker.infra.controller.course.request.CreateCourseRequest;
import com.example.api_docker.infra.controller.course.request.DefineAssessmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CreateCourseUseCase createCourseUseCase;
    private final AddModuleUseCase addModuleUseCase;
    private final AddLessonUseCase addLessonUseCase;
    private final DefineAssessmentUseCase defineAssessmentUseCase;
    private final PublishCourseUseCase publishCourseUseCase;
    private final GetCourseUseCase getCourseUseCase;
    private final ListCoursesUseCase listCoursesUseCase;

    @PostMapping
    public ResponseEntity<CourseResult> create(@RequestBody @Valid CreateCourseRequest request) {
        CreateCourseCommand command = new CreateCourseCommand(
                request.title(),
                request.description(),
                request.instructorId(),
                request.price(),
                request.currency(),
                request.estimatedHours()
        );

        CourseResult result = createCourseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<Void> addModule(@PathVariable UUID courseId, @RequestBody @Valid AddModuleRequest request) {
        AddModuleCommand command = new AddModuleCommand(
                new CourseId(courseId),
                request.title(),
                request.order()
        );
        addModuleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{courseId}/modules/{moduleId}/lessons")
    public ResponseEntity<Void> addLesson(@PathVariable UUID courseId, @PathVariable UUID moduleId,
            @RequestBody @Valid AddLessonRequest request) {
        AddLessonCommand command = new AddLessonCommand(
                new CourseId(courseId),
                new ModuleId(moduleId),
                request.title(),
                request.order(),
                request.durationMinutes()
        );

        addLessonUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{courseId}/assessment")
    public ResponseEntity<Void> defineAssessment(@PathVariable UUID courseId, @RequestBody @Valid DefineAssessmentRequest request) {
        DefineAssessmentCommand command = new DefineAssessmentCommand(
                new CourseId(courseId),
                request.title(),
                request.minimumGrade(),
                request.maximumGrade()
        );

        defineAssessmentUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/publish")
    public ResponseEntity<Void> publish(@PathVariable UUID courseId) {
        publishCourseUseCase.execute(new PublishCourseCommand(new CourseId(courseId)));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResult> findById(@PathVariable UUID courseId) {
        var result = getCourseUseCase.execute(new GetCourseQuery(new CourseId(courseId)));
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<CourseResult>> listAll() {
        return ResponseEntity.ok(listCoursesUseCase.execute());
    }
}
