package com.example.api_docker.infra.controller.course;

import com.example.api_docker.ControllerAbstractTests;
import com.example.api_docker.application.course.result.CourseResult;
import com.example.api_docker.application.course.usecase.*;
import com.example.api_docker.domain.course.CurrencyType;
import com.example.api_docker.infra.controller.course.request.AddLessonRequest;
import com.example.api_docker.infra.controller.course.request.AddModuleRequest;
import com.example.api_docker.infra.controller.course.request.CreateCourseRequest;
import com.example.api_docker.infra.controller.course.request.DefineAssessmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest extends ControllerAbstractTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCourseUseCase createCourseUseCase;

    @MockitoBean
    private AddModuleUseCase addModuleUseCase;

    @MockitoBean
    private AddLessonUseCase addLessonUseCase;

    @MockitoBean
    private DefineAssessmentUseCase defineAssessmentUseCase;

    @MockitoBean
    private PublishCourseUseCase publishCourseUseCase;

    @MockitoBean
    private GetCourseUseCase getCourseUseCase;

    @MockitoBean
    private ListCoursesUseCase listCoursesUseCase;

    private UUID courseId;
    private UUID moduleId;
    private CourseResult courseResult;

    @BeforeEach
    void setUpCourse() {
        courseId = UUID.randomUUID();
        moduleId = UUID.randomUUID();
        courseResult = new CourseResult(
                courseId,
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                new BigDecimal("199.90"),
                "BRL",
                20,
                "DRAFT",
                List.of(),
                null
        );
    }

    // ─── Criar curso ─────────────────────────────────────────────

    @Test
    void shouldReturn201WhenCreatingCourseWithValidData() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                UUID.randomUUID(),
                new BigDecimal("199.90"),
                CurrencyType.BRL,
                20
        );

        when(createCourseUseCase.execute(any())).thenReturn(courseResult);

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Architecture na Prática"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturn401WhenCreatingCourseWithoutToken() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                UUID.randomUUID(),
                new BigDecimal("199.90"),
                CurrencyType.BRL,
                20
        );

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenCreatingCourseWithStudentToken() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "Clean Architecture na Prática",
                "Aprenda Clean Architecture com Java 21",
                UUID.randomUUID(),
                new BigDecimal("199.90"),
                CurrencyType.BRL,
                20
        );

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoStudent))
                .andExpect(status().isForbidden());
    }

    // ─── Adicionar módulo ────────────────────────────────────────

    @Test
    void shouldReturn201WhenAddingModule() throws Exception {
        AddModuleRequest request = new AddModuleRequest("Fundamentos", 1);

        doNothing().when(addModuleUseCase).execute(any());

        mockMvc.perform(post("/courses/{courseId}/modules", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isCreated());
    }

    // ─── Adicionar aula ──────────────────────────────────────────

    @Test
    void shouldReturn201WhenAddingLesson() throws Exception {
        AddLessonRequest request = new AddLessonRequest("Introdução", 1, 30);

        doNothing().when(addLessonUseCase).execute(any());

        mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons",
                        courseId, moduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isCreated());
    }

    // ─── Definir assessment ──────────────────────────────────────

    @Test
    void shouldReturn200WhenDefiningAssessment() throws Exception {
        DefineAssessmentRequest request = new DefineAssessmentRequest(
                "Prova Final",
                new BigDecimal("6.0"),
                new BigDecimal("10.0")
        );

        doNothing().when(defineAssessmentUseCase).execute(any());

        mockMvc.perform(post("/courses/{courseId}/assessment", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk());
    }

    // ─── Publicar curso ──────────────────────────────────────────

    @Test
    void shouldReturn200WhenPublishingCourse() throws Exception {
        doNothing().when(publishCourseUseCase).execute(any());

        mockMvc.perform(post("/courses/{courseId}/publish", courseId)
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk());
    }

    // ─── Buscar curso por id ─────────────────────────────────────

    @Test
    void shouldReturn200WhenGettingCourseById() throws Exception {
        when(getCourseUseCase.execute(any())).thenReturn(courseResult);

        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture na Prática"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    // ─── Listar cursos ───────────────────────────────────────────

    @Test
    void shouldReturn200WhenListingCourses() throws Exception {
        when(listCoursesUseCase.execute()).thenReturn(List.of(courseResult));

        mockMvc.perform(get("/courses")
                        .header("Authorization", "Bearer " + tokenDoAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture na Prática"))
                .andExpect(jsonPath("$").isArray());
    }
}