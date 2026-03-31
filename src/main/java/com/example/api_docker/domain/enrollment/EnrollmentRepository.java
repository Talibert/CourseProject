package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.student.StudentId;

import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    Optional<Enrollment> findActiveByStudentAndCourse(StudentId studentId, CourseId courseId);
}
