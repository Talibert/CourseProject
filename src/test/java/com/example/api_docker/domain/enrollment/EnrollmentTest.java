package com.example.api_docker.domain.enrollment;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.certificate.CertificatePolicy;
import com.example.api_docker.domain.course.*;
import com.example.api_docker.domain.enrollment.event.*;
import com.example.api_docker.domain.enrollment.exception.EnrollmentCompletionNotAllowedException;
import com.example.api_docker.domain.enrollment.exception.EnrollmentNotActiveException;
import com.example.api_docker.domain.enrollment.exception.InvalidEnrollmentTransitionException;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentTest extends UnitAbstractTests {

    private UserId studentId;
    private CourseId courseId;
    private LessonId lessonId1;
    private LessonId lessonId2;
    private LessonId lessonId3;
    private LessonId lessonId4;
    private CourseStructure courseStructure;

    @BeforeEach
    void setUp() {
        studentId = UserId.generate();
        courseId = CourseId.generate();
        lessonId1 = LessonId.generate();
        lessonId2 = LessonId.generate();
        lessonId3 = LessonId.generate();
        lessonId4 = LessonId.generate();

        ModuleStructure moduleStructure = ModuleStructure.of(
                ModuleId.generate(),
                "Módulo 1",
                List.of(lessonId1, lessonId2, lessonId3, lessonId4)
        );
        courseStructure = CourseStructure.of(courseId, List.of(moduleStructure));
    }

    @Test
    @DisplayName("Deve criar matrícula com sucesso no status PENDING e registrar evento EnrollmentCreatedEvent")
    void shouldCreateEnrollmentSuccessfullyWithCreatedEvent() {
        Enrollment enrollment = Enrollment.create(
                studentId,
                courseId,
                courseStructure,
                new BigDecimal("199.90"),
                PaymentMethodType.CREDIT_CARD,
                1
        );

        assertNotNull(enrollment.getId());
        assertEquals(studentId, enrollment.getUserId());
        assertEquals(courseId, enrollment.getCourseId());
        assertEquals(EnrollmentStatusType.PENDING, enrollment.getStatus());
        assertFalse(enrollment.isActive());
        assertFalse(enrollment.isCompleted());
        assertNotNull(enrollment.getEnrolledAt());
        assertNull(enrollment.getCompletedAt());
        assertEquals(0.0, enrollment.getProgress().percentage());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(EnrollmentCreatedEvent.class, events.getFirst());

        EnrollmentCreatedEvent event = (EnrollmentCreatedEvent) events.getFirst();
        assertEquals(enrollment.getId(), event.enrollmentId());
        assertEquals(studentId, event.studentId());
        assertEquals(courseId, event.courseId());
        assertEquals(new BigDecimal("199.90"), event.amount());
        assertEquals(PaymentMethodType.CREDIT_CARD, event.paymentMethod());
        assertEquals(1, event.installments());
    }

    @Test
    @DisplayName("Deve restaurar matrícula com sucesso utilizando o método de fábrica restore")
    void shouldRestoreEnrollmentSuccessfully() {
        EnrollmentId id = EnrollmentId.generate();
        LocalDateTime enrolledAt = LocalDateTime.now().minusDays(5);
        LocalDateTime completedAt = LocalDateTime.now();
        Progress progress = Progress.zero(4);

        Enrollment enrollment = Enrollment.restore(
                id,
                studentId,
                courseId,
                EnrollmentStatusType.COMPLETED,
                progress,
                enrolledAt,
                completedAt
        );

        assertEquals(id, enrollment.getId());
        assertEquals(studentId, enrollment.getUserId());
        assertEquals(courseId, enrollment.getCourseId());
        assertEquals(EnrollmentStatusType.COMPLETED, enrollment.getStatus());
        assertTrue(enrollment.isCompleted());
        assertEquals(enrolledAt, enrollment.getEnrolledAt());
        assertEquals(completedAt, enrollment.getCompletedAt());
        assertTrue(enrollment.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve ativar matrícula quando o status for PENDING")
    void shouldActivateEnrollmentWhenStatusIsPending() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.pullDomainEvents(); // limpa evento de criação

        enrollment.activate();

        assertEquals(EnrollmentStatusType.ACTIVE, enrollment.getStatus());
        assertTrue(enrollment.isActive());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(EnrollmentActivatedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar ativar matrícula que não está no status PENDING")
    void shouldThrowExceptionWhenActivatingNonPendingEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate(); // passa para ACTIVE

        InvalidEnrollmentTransitionException exception = assertThrows(
                InvalidEnrollmentTransitionException.class,
                enrollment::activate
        );
        assertTrue(exception.getMessage().contains("ACTIVE"));
    }

    @Test
    @DisplayName("Deve suspender matrícula ativa com sucesso")
    void shouldSuspendActiveEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();
        enrollment.pullDomainEvents();

        enrollment.suspend(SuspensionReason.PAYMENT_OVERDUE);

        assertEquals(EnrollmentStatusType.SUSPENDED, enrollment.getStatus());
        assertFalse(enrollment.isActive());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(EnrollmentSuspendedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar suspender matrícula que não está ACTIVE")
    void shouldThrowExceptionWhenSuspendingNonActiveEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        ); // Status PENDING

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> enrollment.suspend(SuspensionReason.PAYMENT_OVERDUE)
        );
    }

    @Test
    @DisplayName("Deve reativar matrícula suspensa com sucesso")
    void shouldReactivateSuspendedEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();
        enrollment.suspend(SuspensionReason.PAYMENT_OVERDUE);
        enrollment.pullDomainEvents();

        enrollment.reactivate();

        assertEquals(EnrollmentStatusType.ACTIVE, enrollment.getStatus());
        assertTrue(enrollment.isActive());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(EnrollmentReactivatedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reativar matrícula que não está SUSPENDED")
    void shouldThrowExceptionWhenReactivatingNonSuspendedEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                enrollment::reactivate
        );
    }

    @Test
    @DisplayName("Deve cancelar matrícula a partir dos status PENDING, ACTIVE e SUSPENDED")
    void shouldCancelEnrollmentFromAllowedStates() {
        Enrollment pendingEnrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        pendingEnrollment.cancel(CancellationReason.STUDENT_REQUEST);
        assertEquals(EnrollmentStatusType.CANCELLED, pendingEnrollment.getStatus());

        Enrollment activeEnrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        activeEnrollment.activate();
        activeEnrollment.cancel(CancellationReason.PAYMENT_OVERDUE);
        assertEquals(EnrollmentStatusType.CANCELLED, activeEnrollment.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar matrícula COMPLETED ou já CANCELLED")
    void shouldThrowExceptionWhenCancellingCompletedOrCancelledEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.cancel(CancellationReason.STUDENT_REQUEST); // CANCELLED

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> enrollment.cancel(CancellationReason.STUDENT_REQUEST)
        );
    }

    @Test
    @DisplayName("Deve concluir matrícula ativa quando a política de certificado for satisfeita")
    void shouldCompleteActiveEnrollmentWhenPolicySatisfied() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();
        enrollment.pullDomainEvents();

        CertificatePolicy mockPolicy = mock(CertificatePolicy.class);
        when(mockPolicy.isSatisfiedBy(enrollment)).thenReturn(true);

        enrollment.complete(mockPolicy);

        assertEquals(EnrollmentStatusType.COMPLETED, enrollment.getStatus());
        assertTrue(enrollment.isCompleted());
        assertNotNull(enrollment.getCompletedAt());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(EnrollmentCompletedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar concluir matrícula quando a política de certificado não for satisfeita")
    void shouldThrowExceptionWhenPolicyNotSatisfied() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();

        CertificatePolicy mockPolicy = mock(CertificatePolicy.class);
        when(mockPolicy.isSatisfiedBy(enrollment)).thenReturn(false);

        EnrollmentCompletionNotAllowedException exception = assertThrows(
                EnrollmentCompletionNotAllowedException.class,
                () -> enrollment.complete(mockPolicy)
        );
        assertTrue(exception.getMessage().contains("Progresso insuficiente"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar concluir matrícula que não está ACTIVE")
    void shouldThrowExceptionWhenCompletingNonActiveEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        ); // Status PENDING

        CertificatePolicy mockPolicy = mock(CertificatePolicy.class);

        assertThrows(
                InvalidEnrollmentTransitionException.class,
                () -> enrollment.complete(mockPolicy)
        );
    }

    @Test
    @DisplayName("Deve registrar progresso de aula na matrícula ativa")
    void shouldRecordLessonProgressSuccessfullyWhenActive() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );
        enrollment.activate();
        enrollment.pullDomainEvents();

        enrollment.recordLessonProgress(lessonId1, courseStructure);

        assertEquals(25.0, enrollment.getProgress().percentage());

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ProgressMilestoneReachedEvent.class, events.getFirst());

        ProgressMilestoneReachedEvent event = (ProgressMilestoneReachedEvent) events.getFirst();
        assertEquals(enrollment.getId(), event.enrollmentId());
        assertEquals(25.0, event.milestone());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar progresso de aula em matrícula que não está ACTIVE")
    void shouldThrowExceptionWhenRecordingProgressOnNonActiveEnrollment() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        ); // Status PENDING

        assertThrows(
                EnrollmentNotActiveException.class,
                () -> enrollment.recordLessonProgress(lessonId1, courseStructure)
        );
    }

    @Test
    @DisplayName("Deve limpar a lista de eventos de domínio ao chamar pullDomainEvents")
    void shouldPullDomainEventsAndClearList() {
        Enrollment enrollment = Enrollment.create(
                studentId, courseId, courseStructure, new BigDecimal("100.00"), PaymentMethodType.PIX, 1
        );

        List<DomainEvent> events = enrollment.pullDomainEvents();
        assertEquals(1, events.size());

        assertTrue(enrollment.pullDomainEvents().isEmpty());
    }
}
