package com.example.api_docker.application.student.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.student.command.RegisterStudentCommand;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.student.event.StudentRegisteredEvent;
import com.example.api_docker.domain.student.exception.CpfAlreadyInUseException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.UserRepository;
import com.example.api_docker.domain.user.exception.EmailAlreadyInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterStudentUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RegisterStudentUseCase registerStudentUseCase;

    private RegisterStudentCommand command;

    private static final String HASHED_PASSWORD = "hash-senha123";

    @BeforeEach
    void setUp() {
        command = new RegisterStudentCommand(
                "Guilherme",
                "Taliberti",
                "guilherme@email.com",
                "529.982.247-25",
                LocalDate.of(2000, 1, 1),
                "senha123"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> registerStudentUseCase.execute(command)
        );

        verify(studentRepository, never()).existsByCpf(any(Cpf.class));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já está em uso")
    void shouldThrowExceptionWhenCpfAlreadyInUse() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(studentRepository.existsByCpf(new Cpf(command.cpf()))).thenReturn(true);

        assertThrows(
                CpfAlreadyInUseException.class,
                () -> registerStudentUseCase.execute(command)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve salvar o student no repositório")
    void shouldSaveStudentInRepository() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(studentRepository.existsByCpf(new Cpf(command.cpf()))).thenReturn(false);
        when(passwordEncoder.encode(command.rawPassword())).thenReturn(HASHED_PASSWORD);

        registerStudentUseCase.execute(command);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());

        Student savedStudent = captor.getValue();
        assertEquals("guilherme@email.com", savedStudent.getEmail().value());
        assertEquals("Guilherme", savedStudent.getName().firstName());
        assertEquals("Taliberti", savedStudent.getName().lastName());
        assertEquals(StudentStatus.ACTIVE, savedStudent.getStatus());
        verify(eventPublisher, times(1)).publish(any(StudentRegisteredEvent.class));
    }
}