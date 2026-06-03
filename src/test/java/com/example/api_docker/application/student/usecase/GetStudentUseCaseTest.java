package com.example.api_docker.application.student.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.student.query.GetStudentQuery;
import com.example.api_docker.application.student.result.StudentResult;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStudentUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GetStudentUseCase getStudentUseCase;

    private UserId studentId;
    private Student student;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        studentId = new UserId(UUID.randomUUID());
        createdAt = LocalDateTime.now();

        student = Student.restore(
                studentId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                "hash-senha",
                StudentStatus.ACTIVE,
                createdAt
        );
    }

    @Test
    @DisplayName("Deve retornar student quando encontrado pelo id")
    void shouldReturnStudentWhenFoundById() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        StudentResult result = getStudentUseCase.execute(new GetStudentQuery(studentId));

        assertNotNull(result);
        assertEquals(studentId.value(), result.userId());
        assertEquals("Guilherme Taliberti", result.fullName());
        assertEquals("guilherme@email.com", result.email());
        assertEquals(StudentStatus.ACTIVE, result.status());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando student não encontrado")
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> getStudentUseCase.execute(new GetStudentQuery(studentId))
        );
    }
}