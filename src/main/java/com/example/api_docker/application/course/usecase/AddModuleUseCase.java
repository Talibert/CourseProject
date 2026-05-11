package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.command.AddModuleCommand;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddModuleUseCase {

    private final CourseRepository courseRepository;

    public void execute(AddModuleCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException(command.courseId()));

        course.addModule(new Module(command.title(), command.order()));
        courseRepository.save(course);
    }
}
