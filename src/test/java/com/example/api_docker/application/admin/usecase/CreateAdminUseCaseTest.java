package com.example.api_docker.application.admin.usecase;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.admin.command.CreateAdminCommand;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.admin.event.AdminCreatedEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.PasswordEncoder;
import com.example.api_docker.domain.user.UserRepository;
import com.example.api_docker.domain.user.exception.EmailAlreadyInUseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdminUseCaseTest extends UnitAbstractTests {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Spy
    @InjectMocks
    private CreateAdminUseCase createAdminUseCase;

    @Test
    @DisplayName("Deve criar admin com dados válidos")
    void shouldCreateAdminWithValidData() {
        CreateAdminCommand command = new CreateAdminCommand(
                "João", "Silva", "joao@email.com", "senha123"
        );

        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.rawPassword())).thenReturn("hash-senha");

        createAdminUseCase.execute(command);

        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {
        CreateAdminCommand command = new CreateAdminCommand(
                "João", "Silva", "joao@email.com", "senha123"
        );

        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> createAdminUseCase.execute(command)
        );

        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve salvar o admin no repositório após criação")
    void shouldSaveAdminInRepository() {
        CreateAdminCommand command = new CreateAdminCommand(
                "João", "Silva", "joao@email.com", "senha123"
        );

        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.rawPassword())).thenReturn("hash-senha");

        createAdminUseCase.execute(command);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());

        Admin savedAdmin = captor.getValue();
        assertEquals("joao@email.com", savedAdmin.getEmail().value());
        assertEquals("João", savedAdmin.getName().firstName());
        assertEquals("Silva", savedAdmin.getName().lastName());
    }

    @Test
    @DisplayName("Deve publicar evento após criar o admin")
    void shouldPublishEventAfterCreatingAdmin() {
        CreateAdminCommand command = new CreateAdminCommand(
                "João", "Silva", "joao@email.com", "senha123"
        );

        when(userRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.rawPassword())).thenReturn("hash-senha");

        createAdminUseCase.execute(command);

        verify(eventPublisher, times(1)).publish(any(AdminCreatedEvent.class));
    }
}