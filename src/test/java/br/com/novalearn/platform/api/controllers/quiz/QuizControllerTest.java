package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.quiz.QuizCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.summary.QuizSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.domain.services.quiz.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.quiz.QuizTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class QuizControllerTest extends BaseControllerTest {
    @MockitoBean
    private QuizService quizService;

    @Test
    void should_list_all_quizzes() throws Exception {
        List<QuizListResponseDTO> response = List.of(
                new QuizListResponseDTO(
                        9L,
                        6L,
                        "First Quiz",
                        10,
                        true
                ),
                new QuizListResponseDTO(
                        11L,
                        6L,
                        "Second Quiz",
                        10,
                        true
                )
        );

        when(quizService.listAllActive()).thenReturn(response);

        mockMvc.perform(get("/api/quizzes").header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));

        verify(quizService).listAllActive();
    }

    @Test
    void should_find_quiz_by_id() throws Exception {
        Long id = 9L;

        QuizResponseDTO response = quizResponse();

        when(quizService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/quizzes/{id}", id)
                .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        verify(quizService).findById(id);
    }

    @Test
    void should_create_quiz() throws Exception {
        QuizCreateRequestDTO request = quizCreateRequest();
        QuizResponseDTO response = quizResponse();

        when(quizService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/quizzes")
                .header("Authorization", "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L));

        verify(quizService).create(any());
    }

    @Test
    void should_update_quiz() throws Exception {
        Long quizId = 9L;
        Long userId = 5L;

        QuizUpdateRequestDTO request = quizUpdateRequest();
        QuizResponseDTO response = new QuizResponseDTO(
                quizId,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                6L,
                "New Quiz",
                "This is the new quiz",
                "Here are some instructions for the quiz",
                15,
                new BigDecimal("70.0"),
                3,
                true,
                userId,
                null
        );

        mock_authenticated_user();

        when(quizService.update(eq(quizId), any(), eq(userId))).thenReturn(response);

        mockMvc.perform(patch("/api/quizzes/{id}", quizId)
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Quiz"));

        verify(quizService).update(eq(quizId), any(), eq(userId));
    }

    @Test
    void should_activate_quiz() throws Exception {
        Long id = 9L;
        Long userId = 5L;

        mock_authenticated_user();

        doNothing().when(quizService).activate(id, userId);

        mockMvc.perform(patch("/api/quizzes/{id}/activate", id)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(quizService).activate(id, userId);
    }

    @Test
    void should_deactivate_quiz() throws Exception {
        Long id = 9L;
        Long userId = 5L;

        mock_authenticated_user();

        doNothing().when(quizService).deactivate(id, userId);

        mockMvc.perform(patch("/api/quizzes/{id}/deactivate", id)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(quizService).deactivate(id, userId);
    }

    @Test
    void should_delete_quiz() throws Exception {
        Long id = 9L;
        Long userId = 5L;

        mock_authenticated_user();

        doNothing().when(quizService).delete(id, userId);

        mockMvc.perform(delete("/api/quizzes/{id}", id)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(quizService).delete(id, userId);
    }

    @Test
    void should_restore_quiz() throws Exception {
        Long id = 9L;
        Long userId = 5L;

        mock_authenticated_user();

        doNothing().when(quizService).restore(id, userId);

        mockMvc.perform(patch("/api/quizzes/{id}/restore", id)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(quizService).restore(id, userId);
    }

    @Test
    void should_list_quizzes_by_module() throws Exception {
        Long moduleId = 6L;

        List<QuizResponseDTO> response = List.of(quizResponse());

        when(quizService.listByModule(moduleId)).thenReturn(response);

        mockMvc.perform(get("/api/quizzes/module/{moduleId}", moduleId)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("First Quiz"));

        verify(quizService).listByModule(moduleId);
    }

    @Test
    void should_get_quiz_summary() throws Exception {
        Long quizId = 9L;

        QuizSummaryResponseDTO response = quizSummaryResponse();

        when(quizService.getSummary(quizId)).thenReturn(response);

        mockMvc.perform(get("/api/quizzes/{quizId}/summary", quizId)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(quizId))
                .andExpect(jsonPath("$.totalQuestions").value(10))
                .andExpect(jsonPath("$.correctAnswers").value(6))
                .andExpect(jsonPath("$.bestScore").value(70.0));

        verify(quizService).getSummary(quizId);
    }

    @Test
    void should_list_my_quiz_attempts() throws Exception {
        Long quizId = 9L;

        List<UserQuizAttemptListResponseDTO> response = List.of(userQuizAttemptListResponse());

        when(quizService.listMyAttempts(quizId)).thenReturn(response);

        mockMvc.perform(get("/api/quizzes/{quizId}/attempts/me", quizId)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].score").value(70.0))
                .andExpect(jsonPath("$[0].status").value("STARTED"));

        verify(quizService).listMyAttempts(quizId);
    }
}