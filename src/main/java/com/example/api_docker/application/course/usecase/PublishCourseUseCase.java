package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.command.PublishCourseCommand;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublishCourseUseCase {

    private final CourseRepository courseRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(PublishCourseCommand command) {
        var course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException(command.courseId()));

        course.publish();
        courseRepository.save(course);
        course.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
