package com.example.api_docker.application.payment.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.payment.command.CreatePaymentCommand;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.payment.Payment;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.payment.PaymentRepository;
import com.example.api_docker.domain.payment.PaymentStatusType;
import com.example.api_docker.domain.payment.event.PaymentCreatedEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.shared.exception.DomainException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest extends UnitAbstractTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CreatePaymentUseCase createPaymentUseCase;

    private EnrollmentId enrollmentId;
    private UserId studentId;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Deve criar pagamento PIX com sucesso")
    void shouldCreatePixPaymentSuccessfully() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.PIX,
                1
        );

        createPaymentUseCase.execute(command);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment savedPayment = captor.getValue();
        assertEquals(enrollmentId, savedPayment.getEnrollmentId());
        assertEquals(studentId, savedPayment.getStudentId());
        assertEquals(new BigDecimal("199.90"), savedPayment.getAmount());
        assertEquals(PaymentMethodType.PIX, savedPayment.getMethod());
        assertEquals(1, savedPayment.getInstallments());
        assertEquals(PaymentStatusType.PENDING, savedPayment.getStatus());
        verify(eventPublisher, times(1)).publish(any(PaymentCreatedEvent.class));
    }

    @Test
    @DisplayName("Deve criar pagamento no cartão parcelado com sucesso")
    void shouldCreateCreditCardInstallmentPaymentSuccessfully() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.CREDIT_CARD,
                6
        );

        createPaymentUseCase.execute(command);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment savedPayment = captor.getValue();
        assertEquals(PaymentMethodType.CREDIT_CARD, savedPayment.getMethod());
        assertEquals(6, savedPayment.getInstallments());
        assertEquals(PaymentStatusType.PENDING, savedPayment.getStatus());
        verify(eventPublisher, times(1)).publish(any(PaymentCreatedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando PIX com mais de uma parcela")
    void shouldThrowExceptionWhenPixWithMoreThanOneInstallment() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.PIX,
                2
        );

        assertThrows(
                DomainException.class,
                () -> createPaymentUseCase.execute(command)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando número de parcelas menor que um")
    void shouldThrowExceptionWhenInstallmentsLessThanOne() {
        CreatePaymentCommand command = new CreatePaymentCommand(
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.CREDIT_CARD,
                0
        );

        assertThrows(
                DomainException.class,
                () -> createPaymentUseCase.execute(command)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
    }
}