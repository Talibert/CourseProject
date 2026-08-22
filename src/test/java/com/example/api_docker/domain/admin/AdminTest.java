package com.example.api_docker.domain.admin;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.domain.admin.event.AdminCreatedEvent;
import com.example.api_docker.domain.admin.event.AdminPasswordChangedEvent;
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

class AdminTest extends UnitAbstractTests {

    @Test
    @DisplayName("Deve criar admin com sucesso e registrar evento AdminCreatedEvent")
    void shouldCreateAdminSuccessfullyWithEvent() {
        FullName name = new FullName("Guilherme", "Taliberti");
        Email email = new Email("admin@email.com");
        String passwordHash = "hash123";

        Admin admin = Admin.create(name, email, passwordHash);

        assertNotNull(admin.getId());
        assertEquals(name, admin.getName());
        assertEquals(email, admin.getEmail());
        assertEquals(passwordHash, admin.getPasswordHash());
        assertNotNull(admin.getCreatedAt());

        List<DomainEvent> events = admin.pullDomainEvents();
        assertEquals(1, events.size());

        AdminCreatedEvent event = (AdminCreatedEvent) events.getFirst();
        assertEquals(admin.getId(), event.userId());
        assertEquals(email, event.email());

        assertTrue(admin.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve restaurar admin sem registrar eventos de domínio")
    void shouldRestoreAdminWithoutDomainEvents() {
        UserId id = UserId.generate();
        FullName name = new FullName("Guilherme", "Taliberti");
        Email email = new Email("admin@email.com");
        String passwordHash = "hash123";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Admin admin = Admin.restore(id, name, email, passwordHash, createdAt);

        assertEquals(id, admin.getId());
        assertEquals(name, admin.getName());
        assertEquals(email, admin.getEmail());
        assertEquals(passwordHash, admin.getPasswordHash());
        assertEquals(createdAt, admin.getCreatedAt());
        assertTrue(admin.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve alterar senha do admin com sucesso e registrar evento AdminPasswordChangedEvent")
    void shouldChangePasswordSuccessfullyWithEvent() {
        Admin admin = Admin.create(
                new FullName("Guilherme", "Taliberti"),
                new Email("admin@email.com"),
                "hash-antigo"
        );
        admin.pullDomainEvents();

        admin.changePassword("hash-novo");

        assertEquals("hash-novo", admin.getPasswordHash());

        List<DomainEvent> events = admin.pullDomainEvents();
        assertEquals(1, events.size());

        AdminPasswordChangedEvent event = (AdminPasswordChangedEvent) events.getFirst();
        assertEquals(admin.getId(), event.userId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar senha para valor nulo ou em branco")
    void shouldThrowExceptionWhenChangingPasswordToInvalidValue() {
        Admin admin = Admin.create(
                new FullName("Guilherme", "Taliberti"),
                new Email("admin@email.com"),
                "hash-antigo"
        );

        DomainException nullException = assertThrows(
                DomainException.class,
                () -> admin.changePassword(null)
        );
        assertEquals("Hash da senha não pode ser vazio", nullException.getMessage());

        DomainException blankException = assertThrows(
                DomainException.class,
                () -> admin.changePassword("   ")
        );
        assertEquals("Hash da senha não pode ser vazio", blankException.getMessage());
    }
}
