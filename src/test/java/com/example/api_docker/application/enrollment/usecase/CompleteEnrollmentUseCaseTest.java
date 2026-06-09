package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.CompleteEnrollmentCommand;
import com.example.api_docker.domain.certificate.CertificatePolicy;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.course.exception.CourseNotFoundException;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.event.EnrollmentCompletedEvent;
import com.example.api_docker.domain.enrollment.exception.EnrollmentCompletionNotAllowedException;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.enrollment.exception.InvalidEnrollmentTransitionException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteEnrollmentUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CertificatePolicy certificatePolicy;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CompleteEnrollmentUseCase completeEnrollmentUseCase;

    private EnrollmentId enrollmentId;
    private CourseId courseId;
    private Enrollment activeEnrollment;
    private Enrollment pendingEnrollment;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());

        activeEnrollment = Enrollment.restore(
                enrollmentId,
                new UserId(UUID.randomUUID()),
                courseId,
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        pendingEnrollment = Enrollment.restore(
                enrollmentId,
                new UserId(UUID.randomUUID()),
                courseId,
                EnrollmentStatusType.PENDING,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        course = Course.restore(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new InstructorId(UUID.randomUUID()),
                Price.of(new BigDecimal("199.90"), CurrencyType.BRL),
                20,
                CourseStatusType.PUBLISHED,
                new ArrayList<>(),
                new Assessment(
                        new AssessmentId(UUID.randomUUID()),
                        "Prova Final",
                        new BigDecimal("6.0"),
                        new BigDecimal("10.0")
                ),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve completar matrícula ativa com sucesso")
    void shouldCompleteActiveEnrollmentSuccessfully() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(activeEnrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(certificatePolicy.isSatisfiedBy(activeEnrollment)).thenReturn(true);

        completeEnrollmentUseCase.execute(new CompleteEnrollmentCommand(enrollmentId));

        assertEquals(EnrollmentStatusType.COMPLETED, activeEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(activeEnrollment);
        verify(eventPublisher, times(1)).publish(any(EnrollmentCompletedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> completeEnrollmentUseCase.execute(new CompleteEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não encontrado")
    void shouldThrowExceptionWhenCourseNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(activeEnrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> completeEnrollmentUseCase.execute(new CompleteEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando critérios de conclusão não satisfeitos")
    void shouldThrowExceptionWhenCompletionCriteriaNotSatisfied() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(activeEnrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(certificatePolicy.isSatisfiedBy(activeEnrollment)).thenReturn(false);

        assertThrows(
                EnrollmentCompletionNotAllowedException.class,
                () -> completeEnrollmentUseCase.execute(new CompleteEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não está ativa")
    void shouldThrowExceptionWhenEnrollmentIsNotActive() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(pendingEnrollment));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> completeEnrollmentUseCase.execute(new CompleteEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }
}