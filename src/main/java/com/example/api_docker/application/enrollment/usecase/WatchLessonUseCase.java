package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.command.WatchLessonCommand;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WatchLessonUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(WatchLessonCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));

        Course course = courseRepository.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(enrollment.getCourseId()));

        CourseStructure structure = course.toStructure();
        enrollment.recordLessonProgress(command.lessonId(), structure);

        enrollmentRepository.save(enrollment);
        enrollment.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
