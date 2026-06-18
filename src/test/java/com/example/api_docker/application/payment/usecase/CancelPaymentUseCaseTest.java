package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.CancelEnrollmentCommand;
import com.example.api_docker.application.enrollment.usecase.CancelEnrollmentUseCase;
import com.example.api_docker.application.payment.command.CancelPaymentCommand;
import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.*;
import com.example.api_docker.domain.payment.event.PaymentCancelledEvent;
import com.example.api_docker.domain.payment.exception.InvalidPaymentTransitionException;
import com.example.api_docker.domain.payment.exception.PaymentNotFoundException;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelPaymentUseCaseTest extends UnitAbstractTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CancelEnrollmentUseCase cancelEnrollmentUseCase;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CancelPaymentUseCase cancelPaymentUseCase;

    private PaymentId paymentId;
    private EnrollmentId enrollmentId;
    private UserId studentId;

    @BeforeEach
    void setUp() {
        paymentId = new PaymentId(UUID.randomUUID());
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
    }

    private Payment buildPayment(PaymentStatusType status) {
        return Payment.restore(
                paymentId,
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.PIX,
                1,
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve cancelar pagamento pendente com sucesso")
    void shouldCancelPendingPaymentSuccessfully() {
        Payment payment = buildPayment(PaymentStatusType.PENDING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        cancelPaymentUseCase.execute(new CancelPaymentCommand(paymentId));

        assertEquals(PaymentStatusType.CANCELLED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(eventPublisher, times(1)).publish(any(PaymentCancelledEvent.class));

        ArgumentCaptor<CancelEnrollmentCommand> captor =
                ArgumentCaptor.forClass(CancelEnrollmentCommand.class);

        verify(cancelEnrollmentUseCase).execute(captor.capture());

        CancelEnrollmentCommand capturedCommand = captor.getValue();
        assertEquals(enrollmentId, capturedCommand.enrollmentId());
        assertEquals(CancellationReason.PAYMENT_OVERDUE, capturedCommand.reason());
    }

    @Test
    @DisplayName("Deve cancelar pagamento em processamento com sucesso")
    void shouldCancelProcessingPaymentSuccessfully() {
        Payment payment = buildPayment(PaymentStatusType.PROCESSING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        cancelPaymentUseCase.execute(new CancelPaymentCommand(paymentId));

        assertEquals(PaymentStatusType.CANCELLED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(eventPublisher, times(1)).publish(any(PaymentCancelledEvent.class));

        ArgumentCaptor<CancelEnrollmentCommand> captor =
                ArgumentCaptor.forClass(CancelEnrollmentCommand.class);

        verify(cancelEnrollmentUseCase).execute(captor.capture());

        CancelEnrollmentCommand capturedCommand = captor.getValue();
        assertEquals(enrollmentId, capturedCommand.enrollmentId());
        assertEquals(CancellationReason.PAYMENT_OVERDUE, capturedCommand.reason());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado")
    void shouldThrowExceptionWhenPaymentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> cancelPaymentUseCase.execute(new CancelPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(cancelEnrollmentUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento já confirmado")
    void shouldThrowExceptionWhenPaymentAlreadyConfirmed() {
        Payment payment = buildPayment(PaymentStatusType.CONFIRMED);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentTransitionException.class,
                () -> cancelPaymentUseCase.execute(new CancelPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(cancelEnrollmentUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento já estornado")
    void shouldThrowExceptionWhenPaymentAlreadyRefunded() {
        Payment payment = buildPayment(PaymentStatusType.REFUNDED);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentTransitionException.class,
                () -> cancelPaymentUseCase.execute(new CancelPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(cancelEnrollmentUseCase, never()).execute(any());
    }
}