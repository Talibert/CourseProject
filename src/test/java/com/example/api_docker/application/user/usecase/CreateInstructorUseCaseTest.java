package com.example.api_docker.application.user.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.user.command.CreateInstructorCommand;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.event.InstructorCreatedEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInstructorUseCaseTest extends UnitAbstractTests {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CreateInstructorUseCase createInstructorUseCase;

    private CreateInstructorCommand command;

    private static final String HASHED_PASSWORD = "hash-senha123";

    @BeforeEach
    void setUp() {
        command = new CreateInstructorCommand(
                "Guilherme",
                "Taliberti",
                "guilherme@email.com",
                "senha123",
                "Especialista em Clean Architecture",
                "Java / Backend"
        );
    }

    @Test
    @DisplayName("Deve criar instructor com dados válidos")
    void shouldCreateInstructorWithValidData() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.rawPassword())).thenReturn(HASHED_PASSWORD);

        createInstructorUseCase.execute(command);

        ArgumentCaptor<Instructor> captor = ArgumentCaptor.forClass(Instructor.class);
        verify(instructorRepository).save(captor.capture());

        Instructor savedInstructor = captor.getValue();
        assertEquals("guilherme@email.com", savedInstructor.getEmail().value());
        assertEquals("Guilherme", savedInstructor.getName().firstName());
        assertEquals("Taliberti", savedInstructor.getName().lastName());
        assertEquals("Especialista em Clean Architecture", savedInstructor.getBio());
        assertEquals("Java / Backend", savedInstructor.getSpecialty());

        verify(eventPublisher, times(1)).publish(any(InstructorCreatedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> createInstructorUseCase.execute(command)
        );

        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    @DisplayName("Não deve salvar se email já existe")
    void shouldNotSaveWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> createInstructorUseCase.execute(command)
        );

        verify(instructorRepository, never()).save(any(Instructor.class));
    }
}