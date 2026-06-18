package com.example.api_docker.infra.controller.enrollment;

import com.example.api_docker.ControllerAbstractTests;
import com.example.api_docker.application.enrollment.result.EnrollmentResult;
import com.example.api_docker.application.enrollment.usecase.*;
import com.example.api_docker.domain.enrollment.CancellationReason;
import com.example.api_docker.domain.enrollment.SuspensionReason;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.infra.controller.enrollment.request.CancelEnrollmentRequest;
import com.example.api_docker.infra.controller.enrollment.request.EnrollStudentRequest;
import com.example.api_docker.infra.controller.enrollment.request.SuspendEnrollmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest extends ControllerAbstractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollStudentUseCase enrollStudentUseCase;

    @MockitoBean
    private ActivateEnrollmentUseCase activateEnrollmentUseCase;

    @MockitoBean
    private SuspendEnrollmentUseCase suspendEnrollmentUseCase;

    @MockitoBean
    private ReactivateEnrollmentUseCase reactivateEnrollmentUseCase;

    @MockitoBean
    private CancelEnrollmentUseCase cancelEnrollmentUseCase;

    @MockitoBean
    private WatchLessonUseCase watchLessonUseCase;

    @MockitoBean
    private CompleteEnrollmentUseCase completeEnrollmentUseCase;

    @MockitoBean
    private GetEnrollmentUseCase getEnrollmentUseCase;

    @MockitoBean
    private ListStudentEnrollmentsUseCase listStudentEnrollmentsUseCase;

    private UUID enrollmentId;
    private UUID courseId;
    private UUID lessonId;
    private EnrollmentResult enrollmentResult;

    @BeforeEach
    void setUpEnrollment() {
        enrollmentId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        lessonId = UUID.randomUUID();

        enrollmentResult = new EnrollmentResult(
                enrollmentId,
                idDoStudent.value(),
                courseId,
                "PENDING",
                0.0,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("POST /enrollments - student matricula com sucesso")
    void shouldEnrollStudentSuccessfully() throws Exception {
        EnrollStudentRequest request = new EnrollStudentRequest(courseId, PaymentMethodType.PIX, 1);

        when(enrollStudentUseCase.execute(any())).thenReturn(enrollmentResult);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.courseId").value(courseId.toString()));
    }

    @Test
    @DisplayName("POST /enrollments - sem token retorna 401")
    void shouldReturn401WhenEnrollingWithoutToken() throws Exception {
        EnrollStudentRequest request = new EnrollStudentRequest(courseId, PaymentMethodType.CREDIT_CARD, 3);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /enrollments - admin tentando matricular retorna 403")
    void shouldReturn403WhenAdminTriesToEnroll() throws Exception {
        EnrollStudentRequest request = new EnrollStudentRequest(courseId, PaymentMethodType.CREDIT_CARD, 12);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /enrollments/my - student lista próprias matrículas")
    void shouldListStudentEnrollmentsSuccessfully() throws Exception {
        when(listStudentEnrollmentsUseCase.execute(any()))
                .thenReturn(List.of(enrollmentResult));

        mockMvc.perform(get("/enrollments/my")
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].enrollmentId").value(enrollmentId.toString()));
    }

    @Test
    @DisplayName("GET /enrollments/my - sem token retorna 401")
    void shouldReturn401WhenListingEnrollmentsWithoutToken() throws Exception {
        mockMvc.perform(get("/enrollments/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /enrollments/{id} - student busca própria matrícula")
    void shouldReturnEnrollmentWhenStudentSearchesById() throws Exception {
        when(getEnrollmentUseCase.execute(any())).thenReturn(enrollmentResult);

        mockMvc.perform(get("/enrollments/{enrollmentId}", enrollmentId)
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(enrollmentId.toString()));
    }

    @Test
    @DisplayName("GET /enrollments/{id} - admin busca matrícula")
    void shouldReturnEnrollmentWhenAdminSearchesById() throws Exception {
        when(getEnrollmentUseCase.execute(any())).thenReturn(enrollmentResult);

        mockMvc.perform(get("/enrollments/{enrollmentId}", enrollmentId)
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(enrollmentId.toString()));
    }

    @Test
    @DisplayName("POST /enrollments/{id}/activate - admin ativa com sucesso")
    void shouldActivateEnrollmentSuccessfully() throws Exception {
        doNothing().when(activateEnrollmentUseCase).execute(any());

        mockMvc.perform(post("/enrollments/{enrollmentId}/activate", enrollmentId)
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enrollments/{id}/activate - student tentando ativar retorna 403")
    void shouldReturn403WhenStudentTriesToActivate() throws Exception {
        mockMvc.perform(post("/enrollments/{enrollmentId}/activate", enrollmentId)
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /enrollments/{id}/suspend - admin suspende com sucesso")
    void shouldSuspendEnrollmentSuccessfully() throws Exception {
        SuspendEnrollmentRequest request = new SuspendEnrollmentRequest(SuspensionReason.PAYMENT_OVERDUE);

        doNothing().when(suspendEnrollmentUseCase).execute(any());

        mockMvc.perform(post("/enrollments/{enrollmentId}/suspend", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enrollments/{id}/cancel - student cancela própria matrícula")
    void shouldCancelEnrollmentSuccessfully() throws Exception {
        CancelEnrollmentRequest request = new CancelEnrollmentRequest(
                CancellationReason.STUDENT_REQUEST
        );

        doNothing().when(cancelEnrollmentUseCase).execute(any());

        mockMvc.perform(post("/enrollments/{enrollmentId}/cancel", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enrollments/{id}/lessons/{id}/watch - student registra progresso")
    void shouldWatchLessonSuccessfully() throws Exception {
        doNothing().when(watchLessonUseCase).execute(any());

        mockMvc.perform(post("/enrollments/{enrollmentId}/lessons/{lessonId}/watch",
                        enrollmentId, lessonId)
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enrollments/{id}/complete - student completa matrícula")
    void shouldCompleteEnrollmentSuccessfully() throws Exception {
        doNothing().when(completeEnrollmentUseCase).execute(any());

        mockMvc.perform(post("/enrollments/{enrollmentId}/complete", enrollmentId)
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isOk());
    }
}