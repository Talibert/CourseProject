package com.example.api_docker.application.student.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.student.command.BanStudentCommand;
import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.enrollment.*;
import com.example.api_docker.domain.enrollment.event.EnrollmentCancelledEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.student.event.StudentBannedEvent;
import com.example.api_docker.domain.student.exception.InvalidStudentTransitionException;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanStudentUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private BanStudentUseCase banStudentUseCase;

    private UserId studentId;
    private Student activeStudent;
    private Student bannedStudent;

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());

        activeStudent = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                "hash-senha",
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );

        bannedStudent = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                "hash-senha",
                StudentStatus.BANNED,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve banir student com sucesso")
    void shouldBanStudentSuccessfully() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(activeStudent));
        when(enrollmentRepository.findActiveByStudentId(studentId.value())).thenReturn(List.of());

        banStudentUseCase.execute(new BanStudentCommand(studentId));

        assertEquals(StudentStatus.BANNED, activeStudent.getStatus());
        verify(studentRepository, times(1)).save(activeStudent);
        verify(eventPublisher, times(1)).publish(any(StudentBannedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student não encontrado")
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> banStudentUseCase.execute(new BanStudentCommand(studentId))
        );

        verify(enrollmentRepository, never()).findActiveByStudentId(any(UUID.class));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Deve cancelar todas as matrículas ativas ao banir")
    void shouldCancelAllActiveEnrollmentsWhenBanning() {
        Enrollment firstEnrollment = Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                new CourseId(UUID.randomUUID()),
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        Enrollment secondEnrollment = Enrollment.restore(
                new EnrollmentId(UUID.randomUUID()),
                studentId,
                new CourseId(UUID.randomUUID()),
                EnrollmentStatusType.ACTIVE,
                Progress.zero(10),
                LocalDateTime.now(),
                null
        );

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(activeStudent));
        when(enrollmentRepository.findActiveByStudentId(studentId.value()))
                .thenReturn(List.of(firstEnrollment, secondEnrollment));

        banStudentUseCase.execute(new BanStudentCommand(studentId));

        assertEquals(EnrollmentStatusType.CANCELLED, firstEnrollment.getStatus());
        assertEquals(EnrollmentStatusType.CANCELLED, secondEnrollment.getStatus());
        verify(enrollmentRepository, times(2)).save(any(Enrollment.class));
        verify(eventPublisher, times(2)).publish(any(EnrollmentCancelledEvent.class));
    }

    @Test
    @DisplayName("Não deve banir student já banido")
    void shouldNotBanAlreadyBannedStudent() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(bannedStudent));

        assertThrows(
                InvalidStudentTransitionException.class,
                () -> banStudentUseCase.execute(new BanStudentCommand(studentId))
        );

        verify(studentRepository, never()).save(any(Student.class));
    }
}