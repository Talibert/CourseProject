package com.example.api_docker.application.auth;

import com.example.api_docker.UnitAbstractTests;
import com.example.api_docker.application.shared.LoginCommand;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.student.Cpf;
import com.example.api_docker.domain.student.Student;
import com.example.api_docker.domain.student.StudentRepository;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.user.*;
import com.example.api_docker.domain.user.exception.InvalidCredentialsException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest extends UnitAbstractTests {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private static final String RAW_PASSWORD = "senha123";
    private static final String HASHED_PASSWORD = "hash-senha123";
    private static final String TOKEN = "jwt-token-gerado";

    @Test
    @DisplayName("Deve autenticar student com credenciais válidas")
    void shouldAuthenticateStudentWithValidCredentials() {
        Email email = new Email("guilherme@email.com");
        Student student = Student.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                HASHED_PASSWORD,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(tokenGenerator.generate(student.getId(), email, UserRole.STUDENT)).thenReturn(TOKEN);

        LoginResult result = loginUseCase.execute(new LoginCommand("guilherme@email.com", RAW_PASSWORD));

        assertEquals(TOKEN, result.token());
        assertEquals("STUDENT", result.role());
        assertEquals("Guilherme Taliberti", result.fullName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta para student")
    void shouldThrowExceptionWhenPasswordIsIncorrectForStudent() {
        Email email = new Email("guilherme@email.com");
        Student student = Student.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                HASHED_PASSWORD,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("senha-errada", HASHED_PASSWORD)).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(new LoginCommand("guilherme@email.com", "senha-errada"))
        );
    }

    @Test
    @DisplayName("Deve autenticar instructor com credenciais válidas")
    void shouldAuthenticateInstructorWithValidCredentials() {
        Email email = new Email("guilherme@email.com");
        Instructor instructor = Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                HASHED_PASSWORD,
                "Especialista em Clean Architecture",
                "Java / Backend",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(email)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(tokenGenerator.generate(instructor.getId(), email, UserRole.INSTRUCTOR)).thenReturn(TOKEN);

        LoginResult result = loginUseCase.execute(new LoginCommand("guilherme@email.com", RAW_PASSWORD));

        assertEquals(TOKEN, result.token());
        assertEquals("INSTRUCTOR", result.role());
        assertEquals("Guilherme Taliberti", result.fullName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta para instructor")
    void shouldThrowExceptionWhenPasswordIsIncorrectForInstructor() {
        Email email = new Email("guilherme@email.com");
        Instructor instructor = Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                HASHED_PASSWORD,
                "Especialista em Clean Architecture",
                "Java / Backend",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(email)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("senha-errada", HASHED_PASSWORD)).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(new LoginCommand("guilherme@email.com", "senha-errada"))
        );
    }

    @Test
    @DisplayName("Deve autenticar admin com credenciais válidas")
    void shouldAuthenticateAdminWithValidCredentials() {
        Email email = new Email("guilherme@email.com");
        Admin admin = Admin.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                HASHED_PASSWORD,
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(tokenGenerator.generate(admin.getId(), email, UserRole.ADMIN)).thenReturn(TOKEN);

        LoginResult result = loginUseCase.execute(new LoginCommand("guilherme@email.com", RAW_PASSWORD));

        assertEquals(TOKEN, result.token());
        assertEquals("ADMIN", result.role());
        assertEquals("Guilherme Taliberti", result.fullName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta para admin")
    void shouldThrowExceptionWhenPasswordIsIncorrectForAdmin() {
        Email email = new Email("guilherme@email.com");
        Admin admin = Admin.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                email,
                HASHED_PASSWORD,
                LocalDateTime.now()
        );

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("senha-errada", HASHED_PASSWORD)).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(new LoginCommand("guilherme@email.com", "senha-errada"))
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        Email email = new Email("guilherme@email.com");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(new LoginCommand("guilherme@email.com", RAW_PASSWORD))
        );
    }

    @Test
    @DisplayName("Deve retornar token com role correta para cada tipo de usuário")
    void shouldReturnTokenWithCorrectRoleForEachUserType() {
        Email studentEmail = new Email("guilherme.student@email.com");
        Email instructorEmail = new Email("guilherme.instructor@email.com");
        Email adminEmail = new Email("guilherme.admin@email.com");

        Student student = Student.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                studentEmail,
                new Cpf("529.982.247-25"),
                LocalDate.of(2000, 1, 1),
                HASHED_PASSWORD,
                StudentStatus.ACTIVE,
                LocalDateTime.now()
        );

        Instructor instructor = Instructor.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                instructorEmail,
                HASHED_PASSWORD,
                "Bio",
                "Specialty",
                null,
                SocialLinks.empty(),
                LocalDateTime.now()
        );

        Admin admin = Admin.restore(
                new UserId(UUID.randomUUID()),
                new FullName("Guilherme", "Taliberti"),
                adminEmail,
                HASHED_PASSWORD,
                LocalDateTime.now()
        );

        // Student
        when(studentRepository.findByEmail(studentEmail)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(tokenGenerator.generate(any(), any(), any())).thenReturn(TOKEN);

        LoginResult studentResult = loginUseCase.execute(
                new LoginCommand("guilherme.student@email.com", RAW_PASSWORD)
        );
        assertEquals("STUDENT", studentResult.role());

        // Instructor
        when(studentRepository.findByEmail(instructorEmail)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(instructorEmail)).thenReturn(Optional.of(instructor));

        LoginResult instructorResult = loginUseCase.execute(
                new LoginCommand("guilherme.instructor@email.com", RAW_PASSWORD)
        );
        assertEquals("INSTRUCTOR", instructorResult.role());

        // Admin
        when(studentRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(instructorRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(adminEmail)).thenReturn(Optional.of(admin));

        LoginResult adminResult = loginUseCase.execute(
                new LoginCommand("guilherme.admin@email.com", RAW_PASSWORD)
        );
        assertEquals("ADMIN", adminResult.role());
    }
}