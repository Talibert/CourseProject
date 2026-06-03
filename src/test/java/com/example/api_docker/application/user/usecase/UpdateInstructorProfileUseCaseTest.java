package com.example.api_docker.application.user.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.user.command.UpdateInstructorProfileCommand;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.instructor.event.InstructorProfileUpdatedEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateInstructorProfileUseCaseTest extends UnitAbstractTests {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private UpdateInstructorProfileUseCase updateInstructorProfileUseCase;

    private UserId instructorId;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        instructorId = new UserId(UUID.randomUUID());

        instructor = Instructor.restore(
                instructorId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                "hash-senha",
                "Bio antiga",
                "Specialty antiga",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve atualizar perfil do instructor com sucesso")
    void shouldUpdateInstructorProfileSuccessfully() {
        UpdateInstructorProfileCommand command = new UpdateInstructorProfileCommand(
                instructorId,
                "Especialista em Clean Architecture",
                "Java / Backend",
                null
        );

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        updateInstructorProfileUseCase.execute(command);

        assertEquals("Especialista em Clean Architecture", instructor.getBio());
        assertEquals("Java / Backend", instructor.getSpecialty());
        assertEquals(SocialLinks.empty(), instructor.getSocialLinks());

        verify(instructorRepository, times(1)).save(instructor);
        verify(eventPublisher, times(1)).publish(any(InstructorProfileUpdatedEvent.class));
    }

    @Test
    @DisplayName("Deve atualizar social links quando fornecidos")
    void shouldUpdateSocialLinksWhenProvided() {
        SocialLinks socialLinks = SocialLinks.of(
                "linkedin.com/guilherme",
                "github.com/guilherme",
                null,
                null
        );

        UpdateInstructorProfileCommand command = new UpdateInstructorProfileCommand(
                instructorId,
                "Especialista em Clean Architecture",
                "Java / Backend",
                socialLinks
        );

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        updateInstructorProfileUseCase.execute(command);

        assertEquals("linkedin.com/guilherme", instructor.getSocialLinks().linkedin());
        assertEquals("github.com/guilherme", instructor.getSocialLinks().github());

        verify(instructorRepository, times(1)).save(instructor);
        verify(eventPublisher, times(1)).publish(any(InstructorProfileUpdatedEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando instructor não encontrado")
    void shouldThrowExceptionWhenInstructorNotFound() {
        UpdateInstructorProfileCommand command = new UpdateInstructorProfileCommand(
                instructorId,
                "Especialista em Clean Architecture",
                "Java / Backend",
                null
        );

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        assertThrows(
                InstructorNotFoundException.class,
                () -> updateInstructorProfileUseCase.execute(command)
        );

        verify(instructorRepository, never()).save(any(Instructor.class));
    }
}