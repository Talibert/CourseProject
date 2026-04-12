package com.example.api_docker.infra.persistence.certificate;

import com.example.api_docker.domain.certificate.Certificate;
import com.example.api_docker.domain.certificate.CertificateId;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.student.StudentId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CertificateJpaRepositoryImpl implements CertificateRepository {

    private final CertificateJpaRepository jpaRepository;

    public CertificateJpaRepositoryImpl(CertificateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Certificate certificate) {
        jpaRepository.save(toJpaEntity(certificate));
    }

    @Override
    public Optional<Certificate> findById(CertificateId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    private CertificateJpaEntity toJpaEntity(Certificate certificate) {
        var entity = new CertificateJpaEntity();
        entity.setId(certificate.getId().value());
        entity.setEnrollmentId(certificate.getEnrollmentId().value());
        entity.setStudentId(certificate.getStudentId().value());
        entity.setCourseId(certificate.getCourseId().value());
        entity.setVerificationCode(certificate.getVerificationCode());
        entity.setIssuedAt(certificate.getIssuedAt());
        return entity;
    }

    private Certificate toDomain(CertificateJpaEntity entity) {
        return Certificate.restore(
                new CertificateId(entity.getId()),
                new EnrollmentId(entity.getEnrollmentId()),
                new StudentId(entity.getStudentId()),
                new CourseId(entity.getCourseId()),
                entity.getVerificationCode(),
                entity.getIssuedAt()
        );
    }
}
