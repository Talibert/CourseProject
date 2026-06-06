package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.application.enrollment.command.EnrollStudentCommand;
import com.example.api_docker.application.enrollment.exception.CourseNotPublishedException;
import com.example.api_docker.application.enrollment.exception.EnrollmentAlreadyExistsException;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.domain.course.Course;
import com.example.api_docker.domain.course.CourseRepository;
import com.example.api_docker.domain.course.CourseStatusType;
import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.enrollment.Enrollment;
import com.example.api_docker.domain.enrollment.EnrollmentRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollStudentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final DomainEventPublisher eventPublisher;

    public EnrollmentResult execute(EnrollStudentCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException(command.courseId()));

        if (course.getStatus() != CourseStatusType.PUBLISHED)
            throw new CourseNotPublishedException(command.courseId());

        if (enrollmentRepository.existsActiveByStudentAndCourse(command.studentId().value(), command.courseId().value()))
            throw new EnrollmentAlreadyExistsException(command.studentId(), command.courseId());

        CourseStructure structure = course.toStructure();
        Enrollment enrollment = Enrollment.create(command.studentId(), command.courseId(), structure);

        enrollmentRepository.save(enrollment);
        enrollment.pullDomainEvents().forEach(eventPublisher::publish);

        return EnrollmentResult.from(enrollment);
    }
}
