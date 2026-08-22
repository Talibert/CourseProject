package com.example.api_docker.domain.instructor;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.instructor.event.InstructorCreatedEvent;
import com.example.api_docker.domain.instructor.event.InstructorPasswordChangedEvent;
import com.example.api_docker.domain.instructor.event.InstructorProfileUpdatedEvent;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstructorTest extends UnitAbstractTests {

    @Test
    @DisplayName("Deve criar instrutor com sucesso e registrar o evento InstructorCreatedEvent")
    void shouldCreateInstructorSuccessfullyWithEvent() {
        FullName name = new FullName("Carlos", "Silva");
        Email email = new Email("carlos.silva@email.com");
        String passwordHash = "hash123";
        String bio = "Instrutor especialista em Java e Docker";
        String specialty = "Backend";

        Instructor instructor = Instructor.create(name, email, passwordHash, bio, specialty);

        assertNotNull(instructor.getId());
        assertEquals(name, instructor.getName());
        assertEquals(email, instructor.getEmail());
        assertEquals(passwordHash, instructor.getPasswordHash());
        assertEquals(bio, instructor.getBio());
        assertEquals(specialty, instructor.getSpecialty());
        assertNull(instructor.getProfilePicture());
        assertEquals(SocialLinks.empty(), instructor.getSocialLinks());
        assertNotNull(instructor.getCreatedAt());

        List<DomainEvent> events = instructor.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(InstructorCreatedEvent.class, events.getFirst());

        InstructorCreatedEvent event = (InstructorCreatedEvent) events.getFirst();
        assertEquals(instructor.getId(), event.userId());
        assertEquals(email, event.email());

        assertTrue(instructor.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve restaurar instrutor com sucesso sem registrar eventos de domínio")
    void shouldRestoreInstructorWithoutEvents() {
        UserId id = UserId.generate();
        FullName name = new FullName("Carlos", "Silva");
        Email email = new Email("carlos.silva@email.com");
        String passwordHash = "hash123";
        String bio = "Instrutor com experiência em Cloud";
        String specialty = "DevOps";
        String profilePicture = "https://example.com/carlos.jpg";
        SocialLinks socialLinks = SocialLinks.of(
                "linkedin.com/in/carlos", "github.com/carlos", "youtube.com/carlos", "instagram.com/carlos"
        );
        LocalDateTime createdAt = LocalDateTime.now().minusDays(10);

        Instructor instructor = Instructor.restore(
                id, name, email, passwordHash, bio, specialty, profilePicture, socialLinks, createdAt
        );

        assertEquals(id, instructor.getId());
        assertEquals(name, instructor.getName());
        assertEquals(email, instructor.getEmail());
        assertEquals(passwordHash, instructor.getPasswordHash());
        assertEquals(bio, instructor.getBio());
        assertEquals(specialty, instructor.getSpecialty());
        assertEquals(profilePicture, instructor.getProfilePicture());
        assertEquals(socialLinks, instructor.getSocialLinks());
        assertEquals(createdAt, instructor.getCreatedAt());
        assertTrue(instructor.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar o perfil do instrutor com sucesso e registrar o evento InstructorProfileUpdatedEvent")
    void shouldUpdateProfileSuccessfullyWithEvent() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash123",
                "Bio Antiga",
                "Especialidade Antiga"
        );
        instructor.pullDomainEvents(); // limpa evento de criação

        instructor.updateProfile("Nova Bio de Especialista", "Arquitetura de Software");

        assertEquals("Nova Bio de Especialista", instructor.getBio());
        assertEquals("Arquitetura de Software", instructor.getSpecialty());

        List<DomainEvent> events = instructor.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(InstructorProfileUpdatedEvent.class, events.getFirst());

        InstructorProfileUpdatedEvent event = (InstructorProfileUpdatedEvent) events.getFirst();
        assertEquals(instructor.getId(), event.userId());
    }

    @Test
    @DisplayName("Deve atualizar a foto de perfil do instrutor com sucesso")
    void shouldUpdateProfilePictureSuccessfully() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash123",
                "Bio",
                "Backend"
        );

        instructor.updateProfilePicture("https://cdn.example.com/photos/carlos.png");

        assertEquals("https://cdn.example.com/photos/carlos.png", instructor.getProfilePicture());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar a foto de perfil com URL nula ou em branco")
    void shouldThrowExceptionWhenUpdatingProfilePictureWithNullOrBlank() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash123",
                "Bio",
                "Backend"
        );

        DomainException nullException = assertThrows(
                DomainException.class,
                () -> instructor.updateProfilePicture(null)
        );
        assertEquals("URL da foto não pode ser vazia", nullException.getMessage());

        DomainException blankException = assertThrows(
                DomainException.class,
                () -> instructor.updateProfilePicture("   ")
        );
        assertEquals("URL da foto não pode ser vazia", blankException.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar os links de redes sociais do instrutor com sucesso")
    void shouldUpdateSocialLinksSuccessfully() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash123",
                "Bio",
                "Backend"
        );

        SocialLinks links = SocialLinks.of(
                "https://linkedin.com/in/carlos",
                "https://github.com/carlos",
                "https://youtube.com/@carlos",
                "https://instagram.com/carlos"
        );

        instructor.updateSocialLinks(links);

        assertEquals(links, instructor.getSocialLinks());
        assertEquals("https://linkedin.com/in/carlos", instructor.getSocialLinks().linkedin());
        assertEquals("https://github.com/carlos", instructor.getSocialLinks().github());
    }

    @Test
    @DisplayName("Deve alterar a senha do instrutor com sucesso e registrar o evento InstructorPasswordChangedEvent")
    void shouldChangePasswordSuccessfullyWithEvent() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash-antigo",
                "Bio",
                "Backend"
        );
        instructor.pullDomainEvents();

        instructor.changePassword("hash-novo-seguro");

        assertEquals("hash-novo-seguro", instructor.getPasswordHash());

        List<DomainEvent> events = instructor.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(InstructorPasswordChangedEvent.class, events.getFirst());

        InstructorPasswordChangedEvent event = (InstructorPasswordChangedEvent) events.getFirst();
        assertEquals(instructor.getId(), event.userId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar a senha para um valor nulo ou em branco")
    void shouldThrowExceptionWhenChangingPasswordToNullOrBlank() {
        Instructor instructor = Instructor.create(
                new FullName("Carlos", "Silva"),
                new Email("carlos@email.com"),
                "hash-antigo",
                "Bio",
                "Backend"
        );

        DomainException nullException = assertThrows(
                DomainException.class,
                () -> instructor.changePassword(null)
        );
        assertEquals("Hash da senha não pode ser vazio", nullException.getMessage());

        DomainException blankException = assertThrows(
                DomainException.class,
                () -> instructor.changePassword("   ")
        );
        assertEquals("Hash da senha não pode ser vazio", blankException.getMessage());
    }
}
