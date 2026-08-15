package com.example.api_docker.domain.payment;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.enrollment.EnrollmentId;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private PaymentRepository paymentRepository;

    private EnrollmentId enrollmentId;
    private UserId studentId;

    @BeforeEach
    void setUp() {
        enrollmentId = new EnrollmentId(UUID.randomUUID());
        studentId = new UserId(UUID.randomUUID());
    }

    private Payment buildPayment(PaymentStatusType status) {
        return Payment.restore(
                new PaymentId(UUID.randomUUID()),
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
    @DisplayName("Deve salvar e encontrar pagamento pelo id")
    void shouldSaveAndFindPaymentById() {
        Payment payment = buildPayment(PaymentStatusType.PENDING);
        paymentRepository.save(payment);

        Optional<Payment> found = paymentRepository.findById(payment.getId());

        assertTrue(found.isPresent());
        assertEquals(payment.getId(), found.get().getId());
        assertEquals(enrollmentId, found.get().getEnrollmentId());
        assertEquals(studentId, found.get().getStudentId());
        assertEquals(new BigDecimal("199.90"), found.get().getAmount());
        assertEquals(PaymentMethodType.PIX, found.get().getMethod());
        assertEquals(PaymentStatusType.PENDING, found.get().getStatus());
    }

    @Test
    @DisplayName("Deve salvar e encontrar pagamento pelo enrollmentId")
    void shouldSaveAndFindPaymentByEnrollmentId() {
        Payment payment = buildPayment(PaymentStatusType.PENDING);
        paymentRepository.save(payment);

        Optional<Payment> found = paymentRepository.findByEnrollmentId(enrollmentId);

        assertTrue(found.isPresent());
        assertEquals(enrollmentId, found.get().getEnrollmentId());
    }

    @Test
    @DisplayName("Deve retornar vazio quando pagamento não encontrado pelo id")
    void shouldReturnEmptyWhenPaymentNotFoundById() {
        Optional<Payment> found = paymentRepository.findById(new PaymentId(UUID.randomUUID()));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar vazio quando pagamento não encontrado pelo enrollmentId")
    void shouldReturnEmptyWhenPaymentNotFoundByEnrollmentId() {
        Optional<Payment> found = paymentRepository.findByEnrollmentId(
                new EnrollmentId(UUID.randomUUID())
        );

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve salvar pagamento de cartão parcelado")
    void shouldSaveCreditCardInstallmentPayment() {
        Payment payment = Payment.restore(
                new PaymentId(UUID.randomUUID()),
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.CREDIT_CARD,
                6,
                PaymentStatusType.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        paymentRepository.save(payment);

        Optional<Payment> found = paymentRepository.findById(payment.getId());

        assertTrue(found.isPresent());
        assertEquals(PaymentMethodType.CREDIT_CARD, found.get().getMethod());
        assertEquals(6, found.get().getInstallments());
    }

    @Test
    @DisplayName("Deve atualizar pagamento ao salvar com mesmo id")
    void shouldUpdatePaymentWhenSavingWithSameId() {
        Payment payment = buildPayment(PaymentStatusType.PENDING);
        paymentRepository.save(payment);

        Payment updated = Payment.restore(
                payment.getId(),
                enrollmentId,
                studentId,
                new BigDecimal("199.90"),
                PaymentMethodType.PIX,
                1,
                PaymentStatusType.CONFIRMED,
                payment.getCreatedAt(),
                LocalDateTime.now()
        );
        paymentRepository.save(updated);

        Optional<Payment> found = paymentRepository.findById(payment.getId());

        assertTrue(found.isPresent());
        assertEquals(PaymentStatusType.CONFIRMED, found.get().getStatus());
    }
}