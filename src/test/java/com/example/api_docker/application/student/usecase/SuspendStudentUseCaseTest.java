package com.example.api_docker.application.student.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.student.command.SuspendStudentCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.student.event.StudentSuspendedEvent;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuspendStudentUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private SuspendStudentUseCase suspendStudentUseCase;

    private UserId studentId;
    private Student activeStudent;
    private Student suspendedStudent;
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

        suspendedStudent = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                "hash-senha",
                StudentStatus.SUSPENDED,
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
    @DisplayName("Deve suspender student ativo com sucesso")
    void shouldSuspendActiveStudentSuccessfully() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(activeStudent));

        suspendStudentUseCase.execute(new SuspendStudentCommand(studentId));

        assertEquals(StudentStatus.SUSPENDED, activeStudent.getStatus());
        verify(studentRepository, times(1)).save(activeStudent);
        verify(eventPublisher, times(1)).publish(any(StudentSuspendedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student não encontrado")
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> suspendStudentUseCase.execute(new SuspendStudentCommand(studentId))
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student já está suspenso")
    void shouldThrowExceptionWhenStudentAlreadySuspended() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(suspendedStudent));

        assertThrows(
                InvalidStudentTransitionException.class,
                () -> suspendStudentUseCase.execute(new SuspendStudentCommand(studentId))
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student está banido")
    void shouldThrowExceptionWhenStudentIsBanned() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(bannedStudent));

        assertThrows(
                InvalidStudentTransitionException.class,
                () -> suspendStudentUseCase.execute(new SuspendStudentCommand(studentId))
        );

        verify(studentRepository, never()).save(any(Student.class));
    }
}