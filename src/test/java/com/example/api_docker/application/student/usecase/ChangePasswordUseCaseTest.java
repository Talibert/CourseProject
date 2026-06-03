package com.example.api_docker.application.student.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.student.command.ChangePasswordCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.student.event.StudentPasswordChangedEvent;
import com.example.api_docker.domain.student.exception.StudentNotActiveException;
import com.example.api_docker.domain.student.exception.StudentNotFoundException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.domain.user.exception.InvalidCredentialsException;
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
class ChangePasswordUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ChangePasswordUseCase changePasswordUseCase;

    private UserId studentId;
    private Student activeStudent;
    private Student suspendedStudent;
    private ChangePasswordCommand command;

    private static final String CURRENT_PASSWORD = "senha-atual";
    private static final String NEW_PASSWORD = "nova-senha";
    private static final String HASHED_PASSWORD = "hash-senha-atual";
    private static final String NEW_HASHED_PASSWORD = "hash-nova-senha";

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());

        activeStudent = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                HASHED_PASSWORD,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );

        suspendedStudent = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                HASHED_PASSWORD,
                StudentStatus.SUSPENDED,
                LocalDateTime.now()
        );

        command = new ChangePasswordCommand(studentId, CURRENT_PASSWORD, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void shouldChangePasswordSuccessfully() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(activeStudent));
        when(passwordEncoder.matches(CURRENT_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(NEW_HASHED_PASSWORD);

        changePasswordUseCase.execute(command);

        assertEquals(NEW_HASHED_PASSWORD, activeStudent.getPasswordHash());
        verify(studentRepository, times(1)).save(activeStudent);

        verify(eventPublisher, times(1)).publish(any(StudentPasswordChangedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student não encontrado")
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> changePasswordUseCase.execute(command)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando student não está ativo")
    void shouldThrowExceptionWhenStudentNotActive() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(suspendedStudent));

        assertThrows(
                StudentNotActiveException.class,
                () -> changePasswordUseCase.execute(command)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha atual incorreta")
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(activeStudent));
        when(passwordEncoder.matches(CURRENT_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> changePasswordUseCase.execute(command)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }
}