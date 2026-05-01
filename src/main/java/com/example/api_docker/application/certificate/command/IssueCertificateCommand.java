package com.example.api_docker.application.certificate.command;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.user.UserId;

public record IssueCertificateCommand(EnrollmentId enrollmentId, UserId userId,
                                      CourseId courseId) {}
