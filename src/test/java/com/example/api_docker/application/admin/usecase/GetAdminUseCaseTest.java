package com.example.api_docker.application.admin.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.admin.query.GetAdminQuery;
import com.example.api_docker.application.admin.result.AdminResult;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.admin.exception.AdminNotFoundException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
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
class GetAdminUseCaseTest extends UnitAbstractTests {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private GetAdminUseCase getAdminUseCase;

    @Test
    @DisplayName("Deve retornar admin quando encontrado pelo id")
    void shouldReturnAdminWhenFoundById() {
        UserId adminId = new UserId(UUID.randomUUID());
        LocalDateTime createdAt = LocalDateTime.now();
        Admin admin = Admin.restore(
                adminId,
                new FullName("Guilherme", "Taliberti"),
                new Email("guilherme@email.com"),
                "hash-senha",
                createdAt
        );

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        AdminResult result = getAdminUseCase.execute(new GetAdminQuery(adminId));

        assertNotNull(result);
        assertEquals(adminId.value(), result.adminId());

        assertEquals(adminId.value(), result.adminId());
        assertEquals("Guilherme Taliberti", result.fullName());
        assertEquals("guilherme@email.com", result.email());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando admin não encontrado")
    void shouldThrowExceptionWhenAdminNotFound() {
        UserId adminId = new UserId(UUID.randomUUID());

        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(
                AdminNotFoundException.class,
                () -> getAdminUseCase.execute(new GetAdminQuery(adminId))
        );
    }
}