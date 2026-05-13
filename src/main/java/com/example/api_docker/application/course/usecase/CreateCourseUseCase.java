package com.example.api_docker.application.course.usecase;

import com.example.api_docker.application.course.command.CreateCourseCommand;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.InstructorId;
import com.example.api_docker.domain.course.Price;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateCourseUseCase {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final DomainEventPublisher eventPublisher;

    public CourseResult execute(CreateCourseCommand command) {
        UserId instructorId = new UserId(command.instructorId());

        if (!instructorRepository.existsById(instructorId))
            throw new InstructorNotFoundException(instructorId);

        Course course = Course.create(
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
