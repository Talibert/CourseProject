package com.example.api_docker.infra.controller.student;

import com.example.api_docker.ControllerAbstractTests;
import com.example.api_docker.application.student.result.LoginResult;
import com.example.api_docker.application.student.result.StudentResult;
import com.example.api_docker.application.student.usecase.GetStudentUseCase;
import com.example.api_docker.application.student.usecase.LoginStudentUseCase;
import com.example.api_docker.application.student.usecase.RegisterStudentUseCase;
import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.StudentId;
import com.example.api_docker.domain.student.StudentStatus;
import com.example.api_docker.domain.student.exception.EmailAlreadyInUseException;
import com.example.api_docker.domain.student.exception.InvalidCredentialsException;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.domain.user.UserRole;
import com.example.api_docker.infra.controller.student.request.LoginRequest;
import com.example.api_docker.infra.controller.student.request.RegisterStudentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest extends ControllerAbstractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterStudentUseCase registerStudentUseCase;

    @MockitoBean
    private LoginStudentUseCase loginStudentUseCase;

    @MockitoBean
    private GetStudentUseCase getStudentUseCase;

    // ─── Registro ───────────────────────────────────────────────

    @Test
    void shouldReturn201WhenRegisteringWithValidData() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest(
                "João", "Silva",
                "joao@email.com", "529.982.247-25",
                LocalDate.of(2000, 1, 1), "senha123"
        );

        doNothing().when(registerStudentUseCase).execute(any());

        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyInUse() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest(
                "João", "Silva",
                "joao@email.com", "529.982.247-25",
                LocalDate.of(2000, 1, 1), "senha123"
        );

        doThrow(new EmailAlreadyInUseException("joao@email.com"))
                .when(registerStudentUseCase).execute(any());

        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn400WhenRegisteringWithInvalidFields() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest(
                "", "", "email-invalido", "123",
                null, "123"
        );

        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ─── Login ──────────────────────────────────────────────────

    @Test
    void shouldReturnTokenWhenLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("joao@email.com", "senha123");
        LoginResult loginResult = new LoginResult(
                "jwt-token-gerado", UUID.randomUUID(), "João Silva"
        );

        when(loginStudentUseCase.execute(any())).thenReturn(loginResult);

        mockMvc.perform(post("/students/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-gerado"))
                .andExpect(jsonPath("$.fullName").value("João Silva"));
    }

    @Test
    void shouldReturn401WhenLoginWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest("joao@email.com", "senha-errada");

        doThrow(new InvalidCredentialsException())
                .when(loginStudentUseCase).execute(any());

        mockMvc.perform(post("/students/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    // ─── GET /me ────────────────────────────────────────────────

    @Test
    void shouldReturn200WhenGettingMeWithValidToken() throws Exception {
        UserId userId = new UserId(UUID.randomUUID());
        String token = jwtTokenGenerator.generate(userId, new Email("joao@email.com"), UserRole.STUDENT);

        StudentResult studentResult = new StudentResult(
                userId.value(), "João Silva",
                "joao@email.com", StudentStatus.ACTIVE, LocalDateTime.now()
        );

        when(getStudentUseCase.execute(any())).thenReturn(studentResult);

        mockMvc.perform(get("/students/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void shouldReturn401WhenGettingMeWithoutToken() throws Exception {
        mockMvc.perform(get("/students/me"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET /{id} ──────────────────────────────────────────────

    @Test
    void shouldReturn200WhenGettingStudentById() throws Exception {
        UserId userId = new UserId(UUID.randomUUID());
        String token = jwtTokenGenerator.generate(userId, new Email("joao@email.com"), UserRole.STUDENT);

        StudentResult studentResult = new StudentResult(
                userId.value(), "João Silva",
                "joao@email.com", StudentStatus.ACTIVE, LocalDateTime.now()
        );

        when(getStudentUseCase.execute(any())).thenReturn(studentResult);

        mockMvc.perform(get("/students/{id}", userId.value())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("João Silva"));
    }
}