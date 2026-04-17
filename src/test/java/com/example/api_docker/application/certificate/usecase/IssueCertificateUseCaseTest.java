package com.example.api_docker.application.certificate.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.certificate.command.IssueCertificateCommand;
import com.example.api_docker.domain.certificate.Certificate;
import com.example.api_docker.domain.certificate.CertificateRepository;
import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.StudentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueCertificateUseCaseTest extends UnitAbstractTests {

    @Mock
    private CertificateRepository repository;

    @Mock
    private VerificationCodeGenerator codeGenerator;

    @Mock
    private DomainEventPublisher eventPublisher;

    private IssueCertificateUseCase useCase;

    @BeforeEach
    void setUp(){
        useCase = Mockito.spy(new IssueCertificateUseCase(repository, codeGenerator, eventPublisher));
    }

    @Test
    void shouldIssueCertificateSuccessfully() {
        EnrollmentId enrollmentId = mock(EnrollmentId.class);
        StudentId studentId = mock(StudentId.class);
        CourseId courseId = mock(CourseId.class);
        String verificationCode = "VALID-CODE-2026";

        IssueCertificateCommand command = new IssueCertificateCommand(enrollmentId, studentId, courseId);
        when(codeGenerator.generate()).thenReturn(verificationCode);

        useCase.execute(command);

        ArgumentCaptor<Certificate> certificateCaptor = ArgumentCaptor.forClass(Certificate.class);
        verify(repository).save(certificateCaptor.capture());

        Certificate savedCertificate = certificateCaptor.getValue();
        assertEquals(enrollmentId, savedCertificate.getEnrollmentId());
        assertEquals(studentId, savedCertificate.getStudentId());
        assertEquals(courseId, savedCertificate.getCourseId());
        assertEquals(verificationCode, savedCertificate.getVerificationCode());

        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldPublishDomainEvents() {
        EnrollmentId enrollmentId = mock(EnrollmentId.class);
        StudentId studentId = mock(StudentId.class);
        CourseId courseId = mock(CourseId.class);
        String verificationCode = "VALID-CODE-2026";

        IssueCertificateCommand command = new IssueCertificateCommand(enrollmentId, studentId, courseId);
        when(codeGenerator.generate()).thenReturn(verificationCode);

        useCase.execute(command);

        ArgumentCaptor<CertificateIssuedEvent> eventCaptor = ArgumentCaptor.forClass(CertificateIssuedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        CertificateIssuedEvent publishedEvent = eventCaptor.getValue();

        assertEquals(studentId, publishedEvent.studentId());
        assertEquals(courseId, publishedEvent.courseId());
    }
}