package com.example.api_docker.domain.admin;

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

class AdminRepositoryTest extends RepositoryAbstractTests {

    @Autowired
    private AdminRepository adminRepository;

    private Admin buildAdmin(String email) {
        return Admin.restore(
                new UserId(UUID.randomUUID()),
                new FullName(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME),
                new Email(email),
                DEFAULT_PASSWORD_HASH,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve salvar e encontrar admin pelo id")
    void shouldSaveAndFindAdminById() {
        Admin admin = buildAdmin(DEFAULT_EMAIL);
        adminRepository.save(admin);

        Optional<Admin> found = adminRepository.findById(admin.getId());

        assertTrue(found.isPresent());
        assertEquals(admin.getId(), found.get().getId());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
        assertEquals(DEFAULT_FIRST_NAME, found.get().getName().firstName());
        assertEquals(DEFAULT_LAST_NAME, found.get().getName().lastName());
    }

    @Test
    @DisplayName("Deve salvar e encontrar admin pelo email")
    void shouldSaveAndFindAdminByEmail() {
        Admin admin = buildAdmin(DEFAULT_EMAIL);
        adminRepository.save(admin);

        Optional<Admin> found = adminRepository.findByEmail(new Email(DEFAULT_EMAIL));

        assertTrue(found.isPresent());
        assertEquals(DEFAULT_EMAIL, found.get().getEmail().value());
    }

    @Test
    @DisplayName("Deve retornar vazio quando admin não encontrado pelo id")
    void shouldReturnEmptyWhenAdminNotFoundById() {
        Optional<Admin> found = adminRepository.findById(new UserId(UUID.randomUUID()));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve retornar vazio quando admin não encontrado pelo email")
    void shouldReturnEmptyWhenAdminNotFoundByEmail() {
        Optional<Admin> found = adminRepository.findByEmail(new Email("naoexiste@email.com"));

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar admin ao salvar com mesmo id")
    void shouldUpdateAdminWhenSavingWithSameId() {
        Admin admin = buildAdmin(DEFAULT_EMAIL);
        adminRepository.save(admin);

        Admin updated = Admin.restore(
                admin.getId(),
                new FullName("Novo", "Nome"),
                new Email("novo@email.com"),
                "novo-hash",
                admin.getCreatedAt()
        );
        adminRepository.save(updated);

        Optional<Admin> found = adminRepository.findById(admin.getId());

        assertTrue(found.isPresent());
        assertEquals("Novo", found.get().getName().firstName());
        assertEquals("novo@email.com", found.get().getEmail().value());
    }
}