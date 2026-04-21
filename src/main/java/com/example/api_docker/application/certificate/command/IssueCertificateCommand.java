package com.example.api_docker.application.certificate.command;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.student.StudentId;

public record IssueCertificateCommand(EnrollmentId enrollmentId, StudentId studentId,
                                      CourseId courseId) {}
