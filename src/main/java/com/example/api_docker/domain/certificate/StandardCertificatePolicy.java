package com.example.api_docker.domain.certificate;

import com.example.api_docker.domain.course.CourseStructure;
import com.example.api_docker.domain.enrollment.Enrollment;
import java.math.BigDecimal;

public class StandardCertificatePolicy implements CertificatePolicy {

    private static final double MIN_PROGRESS = 70.0;
    private static final BigDecimal MIN_GRADE = new BigDecimal("6.0");

    @Override
    public boolean isSatisfiedBy(Enrollment enrollment, CourseStructure courseStructure) {
        return enrollment.getProgress().percentage(courseStructure) >= MIN_PROGRESS
                && enrollment.getProgress().hasPassingGrade(MIN_GRADE);
    }
}
