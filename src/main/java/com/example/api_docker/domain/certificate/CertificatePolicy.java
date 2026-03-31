package com.example.api_docker.domain.certificate;

import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.enrollment.Enrollment;

public interface CertificatePolicy {
    boolean isSatisfiedBy(Enrollment enrollment, CourseStructure courseStructure);
}
