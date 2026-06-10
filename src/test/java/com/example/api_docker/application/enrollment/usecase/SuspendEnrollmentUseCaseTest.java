package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.SuspendEnrollmentCommand;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.event.EnrollmentSuspendedEvent;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuspendEnrollmentUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private SuspendEnrollmentUseCase suspendEnrollmentUseCase;

    private EnrollmentId enrollmentId;
    private UserId studentId;
    private CourseId courseId;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
        courseId = new CourseId(UUID.randomUUID());
    }

    private Enrollment buildEnrollment(EnrollmentStatusType status) {
        return Enrollment.restore(
                enrollmentId,
                studentId,
                courseId,
                status,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve suspender matrícula ativa com sucesso")
    void shouldSuspendActiveEnrollmentSuccessfully() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.ACTIVE);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        suspendEnrollmentUseCase.execute(new SuspendEnrollmentCommand(
                enrollmentId, SuspensionReason.PAYMENT_OVERDUE
        ));

        assertEquals(EnrollmentStatusType.SUSPENDED, enrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(enrollment);
        verify(eventPublisher, times(1)).publish(any(EnrollmentSuspendedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> suspendEnrollmentUseCase.execute(new SuspendEnrollmentCommand(
                        enrollmentId, SuspensionReason.PAYMENT_OVERDUE
                ))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não está ativa")
    void shouldThrowExceptionWhenEnrollmentIsNotActive() {
        Enrollment enrollment = buildEnrollment(EnrollmentStatusType.SUSPENDED);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> suspendEnrollmentUseCase.execute(new SuspendEnrollmentCommand(
                        enrollmentId, SuspensionReason.PAYMENT_OVERDUE
                ))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verify(eventPublisher, never()).publish(any());
    }
}