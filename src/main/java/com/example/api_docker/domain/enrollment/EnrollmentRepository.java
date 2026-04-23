package com.example.api_docker.domain.enrollment;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.student.StudentId;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    List<Enrollment> findActiveByStudent(StudentId studentId);
}
