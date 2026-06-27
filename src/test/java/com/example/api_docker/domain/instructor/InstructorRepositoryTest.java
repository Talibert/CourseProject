package com.example.api_docker.domain.instructor;

import com.example.api_docker.RepositoryAbstractTests;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InstructorRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private InstructorRepository instructorRepository;

    private Instructor buildInstructor(String email) {
        return Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email(email),
                DEFAULT_PASSWORD_HASH,
                "Especialista em Clean Architecture",
                "Java / Backend",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar instructor pelo id")
    void shouldSaveAndFindInstructorById() {
        Instructor instructor = buildInstructor(DEFAULT_EMAIL);
        instructorRepository.save(instructor);

        Optional<Instructor> found = instructorRepository.findById(instructor.getId());

        assertTrue(found.isPresent());
        assertEquals(instructor.getId(), found.get().getId());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
        assertEquals(DEFAULT_FIRST_NAME, found.get().getName().firstName());
        assertEquals(DEFAULT_LAST_NAME, found.get().getName().lastName());
        assertEquals("Especialista em Clean Architecture", found.get().getBio());
        assertEquals("Java / Backend", found.get().getSpecialty());
    }

    @Test
    @DisplayName("Deve salvar e encontrar instructor pelo email")
    void shouldSaveAndFindInstructorByEmail() {
        Instructor instructor = buildInstructor(DEFAULT_EMAIL);
        instructorRepository.save(instructor);

        Optional<Instructor> found = instructorRepository.findByEmail(new Email(DEFAULT_EMAIL));

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
    }

    @Test
    @DisplayName("Deve retornar vazio quando instructor não encontrado pelo id")
    void shouldReturnEmptyWhenInstructorNotFoundById() {
        Optional<Instructor> found = instructorRepository.findById(new UserId(UUID.randomUUID()));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar vazio quando instructor não encontrado pelo email")
    void shouldReturnEmptyWhenInstructorNotFoundByEmail() {
        Optional<Instructor> found = instructorRepository.findByEmail(
                new Email("naoexiste@email.com")
        );

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando instructor existe pelo id")
    void shouldReturnTrueWhenInstructorExistsById() {
        Instructor instructor = buildInstructor(DEFAULT_EMAIL);
        instructorRepository.save(instructor);

        boolean exists = instructorRepository.existsById(instructor.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso quando instructor não existe pelo id")
    void shouldReturnFalseWhenInstructorNotExistsById() {
        boolean exists = instructorRepository.existsById(new UserId(UUID.randomUUID()));

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve atualizar instructor ao salvar com mesmo id")
    void shouldUpdateInstructorWhenSavingWithSameId() {
        Instructor instructor = buildInstructor(DEFAULT_EMAIL);
        instructorRepository.save(instructor);

        Instructor updated = Instructor.restore(
                instructor.getId(),
                new FullName("Novo", "Nome"),
                new Email("novo@email.com"),
                DEFAULT_PASSWORD_HASH,
                "Nova bio",
                "Nova specialty",
                null,
                SocialLinks.of("linkedin.com/novo", null, null, null),
                instructor.getCreatedAt()
        );
        instructorRepository.save(updated);

        Optional<Instructor> found = instructorRepository.findById(instructor.getId());

        assertTrue(found.isPresent());
        assertEquals("Novo", found.get().getName().firstName());
        assertEquals("novo@email.com", found.get().getEmail().value());
        assertEquals("Nova bio", found.get().getBio());
        assertEquals("linkedin.com/novo", found.get().getSocialLinks().linkedin());
    }

    @Test
    @DisplayName("Deve preservar social links ao salvar")
    void shouldPreserveSocialLinksWhenSaving() {
        Instructor instructor = Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email(DEFAULT_EMAIL),
                DEFAULT_PASSWORD_HASH,
                "Bio",
                "Specialty",
                null,
                SocialLinks.of(
                        "linkedin.com/guilherme",
                        "github.com/guilherme",
                        "youtube.com/guilherme",
                        "instagram.com/guilherme"
                ),
                LocalDateTime.now()
        );
        instructorRepository.save(instructor);

        Optional<Instructor> found = instructorRepository.findById(instructor.getId());

        assertTrue(found.isPresent());
        assertEquals("linkedin.com/guilherme", found.get().getSocialLinks().linkedin());
        assertEquals("github.com/guilherme", found.get().getSocialLinks().github());
        assertEquals("youtube.com/guilherme", found.get().getSocialLinks().youtube());
        assertEquals("instagram.com/guilherme", found.get().getSocialLinks().instagram());
    }
}