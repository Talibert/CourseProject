package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.CourseResult;
import com.example.api_docker.application.course.command.CreateCourseCommand;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.InstructorId;
import com.example.api_docker.domain.course.Price;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CreateCourseUseCase {

    private final CourseRepository courseRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateCourseUseCase(CourseRepository courseRepository,
                               DomainEventPublisher eventPublisher) {
        this.courseRepository = courseRepository;
        this.eventPublisher = eventPublisher;
    }

    public CourseResult execute(CreateCourseCommand command) {
        var course = Course.create(
                command.title(),
                command.description(),
                new InstructorId(command.instructorId()),
                Price.of(command.price(), command.currency()),
                command.estimatedHours()
        );

        courseRepository.save(course);
        course.pullDomainEvents().forEach(eventPublisher::publish);

        return CourseResult.from(course);
    }
}
