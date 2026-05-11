package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.command.AddLessonCommand;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.Lesson;
import com.example.api_docker.domain.course.Module;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.course.exception.ModuleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddLessonUseCase {

    private final CourseRepository courseRepository;

    public void execute(AddLessonCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException(command.courseId()));

        Module module = course.getModules().stream()
                .filter(m -> m.getId().equals(command.moduleId()))
                .findFirst()
                .orElseThrow(() -> new ModuleNotFoundException(command.moduleId()));

        module.addLesson(new Lesson(
                command.title(),
                command.order(),
                command.durationMinutes()
        ));

        courseRepository.save(course);
    }
}
