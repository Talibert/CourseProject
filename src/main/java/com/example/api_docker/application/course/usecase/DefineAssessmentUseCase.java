package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.command.DefineAssessmentCommand;
import com.example.api_docker.domain.course.Assessment;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefineAssessmentUseCase {

    private final CourseRepository courseRepository;

    public void execute(DefineAssessmentCommand command) {
        var course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException(command.courseId()));

        course.defineAssessment(new Assessment(
                command.title(),
                command.minimumGrade(),
                command.maximumGrade()
        ));

        courseRepository.save(course);
    }
}
