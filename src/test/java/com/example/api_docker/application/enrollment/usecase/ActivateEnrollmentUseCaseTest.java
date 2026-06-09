package com.example.api_docker.application.enrollment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.ActivateEnrollmentCommand;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.event.EnrollmentActivatedEvent;
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
class ActivateEnrollmentUseCaseTest extends UnitAbstractTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ActivateEnrollmentUseCase activateEnrollmentUseCase;

    private EnrollmentId enrollmentId;
    private Enrollment pendingEnrollment;
    private Enrollment activeEnrollment;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());

        pendingEnrollment = Enrollment.restore(
                enrollmentId,
                new UserId(UUID.randomUUID()),
                new CourseId(UUID.randomUUID()),
                EnrollmentStatusType.PENDING,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        activeEnrollment = Enrollment.restore(
                enrollmentId,
                new UserId(UUID.randomUUID()),
                new CourseId(UUID.randomUUID()),
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve ativar matrícula pendente com sucesso")
    void shouldActivatePendingEnrollmentSuccessfully() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(pendingEnrollment));

        activateEnrollmentUseCase.execute(new ActivateEnrollmentCommand(enrollmentId));

        assertEquals(EnrollmentStatusType.ACTIVE, pendingEnrollment.getStatus());

        verify(enrollmentRepository, times(1)).save(pendingEnrollment);
        verify(eventPublisher, times(1)).publish(any(EnrollmentActivatedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não encontrada")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> activateEnrollmentUseCase.execute(new ActivateEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula não está pendente")
    void shouldThrowExceptionWhenEnrollmentIsNotPending() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(activeEnrollment));

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> activateEnrollmentUseCase.execute(new ActivateEnrollmentCommand(enrollmentId))
        );

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}