package com.example.api_docker.domain.enrollment.exception;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.shared.exception.ConflictException;
import com.example.api_docker.domain.user.UserId;

public class EnrollmentAlreadyExistsException extends ConflictException {
    public EnrollmentAlreadyExistsException(UserId studentId, CourseId courseId) {
        super("Student já matriculado neste curso: " + studentId.value());
    }
}
