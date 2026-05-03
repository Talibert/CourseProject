package com.example.api_docker.infra.controller.admin;

import com.example.api_docker.ControllerAbstractTests;
import com.example.api_docker.application.admin.result.AdminResult;
import com.example.api_docker.application.admin.usecase.CreateAdminUseCase;
import com.example.api_docker.application.admin.usecase.GetAdminUseCase;
import com.example.api_docker.application.admin.usecase.LoginAdminUseCase;
import com.example.api_docker.application.shared.LoginResult;
import com.example.api_docker.infra.controller.admin.request.CreateAdminRequest;
import com.example.api_docker.infra.controller.admin.request.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest extends ControllerAbstractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAdminUseCase createAdminUseCase;

    @MockitoBean
    private LoginAdminUseCase loginAdminUseCase;

    @MockitoBean
    private GetAdminUseCase getAdminUseCase;

    @Test
    void shouldReturn201WhenCreatingAdminWithValidData() throws Exception {
        CreateAdminRequest request = getCreateAdminRequest();

        doNothing().when(createAdminUseCase).execute(any());

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin)) // ← token ADMIN
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WhenCreatingAdminWithoutToken() throws Exception {
        CreateAdminRequest request = getCreateAdminRequest();

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenCreatingAdminWithStudentToken() throws Exception {
        CreateAdminRequest request = getCreateAdminRequest();

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnTokenWhenLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest(
                "guilhermenovo@gmail.com", "senha123"
        );

        LoginResult loginResult = new LoginResult(
                "jwt-token-gerado", UUID.randomUUID(), "Guilherme Taliberti"
        );

        when(loginAdminUseCase.execute(any())).thenReturn(loginResult);

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-gerado"))
                .andExpect(jsonPath("$.fullName").value("Guilherme Taliberti"));
    }

    @Test
    void shouldReturn200WhenGettingMe() throws Exception {
        AdminResult adminResult = new AdminResult(
                idDoAdmin.value(), "Guilherme Taliberti",
                "guilhermetaliberti@gmail.com", LocalDateTime.now()
        );

        when(getAdminUseCase.execute(any())).thenReturn(adminResult);

        mockMvc.perform(get("/admin/me")
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Guilherme Taliberti"));
    }

    @Test
    void shouldReturn200WhenGettingAdminById() throws Exception {
        AdminResult adminResult = new AdminResult(
                idDoAdmin.value(), "Guilherme Taliberti",
                "guilhermetaliberti@email.com",  LocalDateTime.now()
        );

        when(getAdminUseCase.execute(any())).thenReturn(adminResult);

        mockMvc.perform(get("/admin/{id}", idDoAdmin.value())
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Guilherme Taliberti"));
    }

    private static CreateAdminRequest getCreateAdminRequest() {
        return new CreateAdminRequest(
                "Guilherme", "Taliberti",
                "guilhermenovo@gmail.com", "senha123"
        );
    }
}
