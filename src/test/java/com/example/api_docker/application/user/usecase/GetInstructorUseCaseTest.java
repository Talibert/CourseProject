package com.example.api_docker.application.user.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.user.query.GetInstructorQuery;
import com.example.api_docker.application.user.result.InstructorResult;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.SocialLinks;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInstructorUseCaseTest extends UnitAbstractTests {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private GetInstructorUseCase getInstructorUseCase;

    private UserId instructorId;
    private Instructor instructor;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        instructorId = new UserId(UUID.randomUUID());
        createdAt = LocalDateTime.now();

        instructor = Instructor.restore(
                instructorId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                "hash-senha",
                "Especialista em Clean Architecture",
                "Java / Backend",
                null,
                SocialLinks.empty(),
                createdAt
        );
    }

    @Test
    @DisplayName("Deve retornar instructor quando encontrado pelo id")
    void shouldReturnInstructorWhenFoundById() {
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        InstructorResult result = getInstructorUseCase.execute(new GetInstructorQuery(instructorId));

        assertNotNull(result);
        assertEquals(instructorId.value(), result.instructorId());
        assertEquals("Guilherme Taliberti", result.fullName());
        assertEquals("guilherme@email.com", result.email());
        assertEquals("Especialista em Clean Architecture", result.bio());
        assertEquals("Java / Backend", result.specialty());
        assertNull(result.profilePicture());
        assertNotNull(result.socialLinks());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando instructor não encontrado")
    void shouldThrowExceptionWhenInstructorNotFound() {
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        assertThrows(
                InstructorNotFoundException.class,
                () -> getInstructorUseCase.execute(new GetInstructorQuery(instructorId))
        );
    }
}