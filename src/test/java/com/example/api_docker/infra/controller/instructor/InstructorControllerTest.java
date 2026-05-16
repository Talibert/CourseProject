package com.example.api_docker.infra.controller.instructor;

import com.example.api_docker.ControllerAbstractTests;
import com.example.api_docker.application.user.result.InstructorResult;
import com.example.api_docker.application.user.result.SocialLinksResult;
import com.example.api_docker.application.user.usecase.CreateInstructorUseCase;
import com.example.api_docker.application.user.usecase.GetInstructorUseCase;
import com.example.api_docker.application.user.usecase.UpdateInstructorProfileUseCase;
import com.example.api_docker.infra.controller.instructor.request.CreateInstructorRequest;
import com.example.api_docker.infra.controller.instructor.request.UpdateInstructorProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest extends ControllerAbstractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateInstructorUseCase createInstructorUseCase;

    @MockitoBean
    private GetInstructorUseCase getInstructorUseCase;

    @MockitoBean
    private UpdateInstructorProfileUseCase updateInstructorProfileUseCase;

    private InstructorResult instructorResult;

    @BeforeEach
    void setUpInstructor() {
        instructorResult = new InstructorResult(
                idDoInstructor.value(),
                "Carlos Silva",
                "instructor@eduflow.com",
                "Especialista em Clean Architecture",
                "Java / Backend",
                null,
                new SocialLinksResult("linkedin.com/carlos", null, null, null),
                LocalDateTime.now()
        );
    }

    // ─── Criar instructor ────────────────────────────────────────

    @Test
    void shouldReturn201WhenCreatingInstructorWithValidData() throws Exception {
        CreateInstructorRequest request = new CreateInstructorRequest(
                "Carlos", "Silva",
                "instructor@eduflow.com",
                "senha123",
                "Especialista em Clean Architecture",
                "Java / Backend"
        );

        doNothing().when(createInstructorUseCase).execute(any());

        mockMvc.perform(post("/instructor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WhenCreatingInstructorWithoutToken() throws Exception {
        CreateInstructorRequest request = new CreateInstructorRequest(
                "Carlos", "Silva",
                "instructor@eduflow.com",
                "senha123",
                "Especialista em Clean Architecture",
                "Java / Backend"
        );

        mockMvc.perform(post("/instructor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenCreatingInstructorWithStudentToken() throws Exception {
        CreateInstructorRequest request = new CreateInstructorRequest(
                "Carlos", "Silva",
                "instructor@eduflow.com",
                "senha123",
                "Especialista em Clean Architecture",
                "Java / Backend"
        );

        mockMvc.perform(post("/instructor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isForbidden());
    }

    // ─── GET /me ────────────────────────────────────────────────

    @Test
    void shouldReturn200WhenGettingMeWithValidToken() throws Exception {
        when(getInstructorUseCase.execute(any())).thenReturn(instructorResult);

        mockMvc.perform(get("/instructor/me")
                        .header("Authorization", "Bearer " + tokenDoInstructor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Carlos Silva"))
                .andExpect(jsonPath("$.specialty").value("Java / Backend"));
    }

    @Test
    void shouldReturn401WhenGettingMeWithoutToken() throws Exception {
        mockMvc.perform(get("/instructor/me"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET /{id} ──────────────────────────────────────────────

    @Test
    void shouldReturn200WhenGettingInstructorById() throws Exception {
        when(getInstructorUseCase.execute(any())).thenReturn(instructorResult);

        mockMvc.perform(get("/instructor/{id}", idDoInstructor.value())
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Carlos Silva"))
                .andExpect(jsonPath("$.email").value("instructor@eduflow.com"));
    }

    // ─── PUT /me/profile ────────────────────────────────────────

    @Test
    void shouldReturn200WhenUpdatingProfile() throws Exception {
        UpdateInstructorProfileRequest request = new UpdateInstructorProfileRequest(
                "Especialista em DDD e Clean Architecture",
                "Java / Backend / Architecture",
                "linkedin.com/carlos",
                "github.com/carlos",
                null,
                null
        );

        doNothing().when(updateInstructorProfileUseCase).execute(any());

        mockMvc.perform(put("/instructor/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoInstructor))
                .andExpect(status().isOk());
    }
}