package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.enrollment.command.ActivateEnrollmentCommand;
import com.example.api_docker.application.enrollment.usecase.ActivateEnrollmentUseCase;
import com.example.api_docker.application.payment.command.ConfirmPaymentCommand;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.*;
import com.example.api_docker.domain.payment.event.PaymentConfirmedEvent;
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
class ConfirmPaymentUseCaseTest extends UnitAbstractTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ActivateEnrollmentUseCase activateEnrollmentUseCase;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ConfirmPaymentUseCase confirmPaymentUseCase;

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
    @DisplayName("Deve confirmar pagamento pendente com sucesso")
    void shouldConfirmPendingPaymentSuccessfully() {
        Payment payment = buildPayment(PaymentStatusType.PENDING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        confirmPaymentUseCase.execute(new ConfirmPaymentCommand(paymentId));

        assertEquals(PaymentStatusType.CONFIRMED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(eventPublisher, times(1)).publish(any(PaymentConfirmedEvent.class));

        ArgumentCaptor<ActivateEnrollmentCommand> captor =
                ArgumentCaptor.forClass(ActivateEnrollmentCommand.class);
        verify(activateEnrollmentUseCase).execute(captor.capture());

        ActivateEnrollmentCommand capturedCommand = captor.getValue();
        assertEquals(enrollmentId, capturedCommand.enrollmentId());
    }

    @Test
    @DisplayName("Deve confirmar pagamento em processamento com sucesso")
    void shouldConfirmProcessingPaymentSuccessfully() {
        Payment payment = buildPayment(PaymentStatusType.PROCESSING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        confirmPaymentUseCase.execute(new ConfirmPaymentCommand(paymentId));

        assertEquals(PaymentStatusType.CONFIRMED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(eventPublisher, times(1)).publish(any(PaymentConfirmedEvent.class));

        ArgumentCaptor<ActivateEnrollmentCommand> captor =
                ArgumentCaptor.forClass(ActivateEnrollmentCommand.class);
        verify(activateEnrollmentUseCase).execute(captor.capture());

        ActivateEnrollmentCommand capturedCommand = captor.getValue();
        assertEquals(enrollmentId, capturedCommand.enrollmentId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado")
    void shouldThrowExceptionWhenPaymentNotFound() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> confirmPaymentUseCase.execute(new ConfirmPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(activateEnrollmentUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento já confirmado")
    void shouldThrowExceptionWhenPaymentAlreadyConfirmed() {
        Payment payment = buildPayment(PaymentStatusType.CONFIRMED);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentTransitionException.class,
                () -> confirmPaymentUseCase.execute(new ConfirmPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(activateEnrollmentUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento cancelado")
    void shouldThrowExceptionWhenPaymentCancelled() {
        Payment payment = buildPayment(PaymentStatusType.CANCELLED);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentTransitionException.class,
                () -> confirmPaymentUseCase.execute(new ConfirmPaymentCommand(paymentId))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
        verify(activateEnrollmentUseCase, never()).execute(any());
    }
}