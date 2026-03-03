package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.quiz.QuizQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.quiz.QuizAnswerOptionTestFactory.quizAnswerOptionResponse;
import static br.com.novalearn.platform.factories.dtos.quiz.QuizQuestionTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizQuestionController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class QuizQuestionControllerTest extends BaseControllerTest {
    @MockitoBean
    private QuizQuestionService quizQuestionService;

    private static final Long QUIZ_ID = 9L;
    private static final Long QUESTION_ID = 20L;

    @Test
    void should_list_all_questions_when_requesting_list() throws Exception {
        when(quizQuestionService.listByQuiz(QUIZ_ID)).thenReturn(List.of(quizQuestionResponse()));

        mockMvc.perform(get("/api/quizzes/{quizId}/questions", QUIZ_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].quizId").value(9L));

        verify(quizQuestionService).listByQuiz(QUIZ_ID);
    }

    @Test
    void should_return_question_when_id_exists() throws Exception {
        when(quizQuestionService.findById(QUESTION_ID)).thenReturn(quizQuestionResponse());

        mockMvc.perform(get("/api/quizzes/{quizId}/questions/{id}", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.quizId").value(9L));

        verify(quizQuestionService).findById(QUESTION_ID);
    }

    @Test
    void should_create_question_when_request_is_valid() throws Exception {
        QuizQuestionCreateRequestDTO request = quizQuestionCreateRequest();

        mock_authenticated_user();

        when(quizQuestionService.create(eq(USER_ID), any())).thenReturn(quizQuestionResponse());

        mockMvc.perform(post("/api/quizzes/{quizId}/questions", QUIZ_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(9L));

        verify(quizQuestionService).create(eq(USER_ID), any(QuizQuestionCreateRequestDTO.class));
    }

    @Test
    void should_update_question_when_request_is_valid() throws Exception {
        QuizQuestionUpdateRequestDTO request = quizQuestionUpdateRequest();

        mock_authenticated_user();

        when(quizQuestionService.update(eq(QUESTION_ID), any(), eq(USER_ID)))
                .thenReturn(new QuizQuestionResponseDTO(
                        20L,
                        true,
                        false,
                        LocalDateTime.now(),
                        null,
                        "Observations",
                        9L,
                        2,
                        "New description",
                        5,
                        5L,
                        null
                ));

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{id}", QUIZ_ID, QUESTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value(2))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.points").value(5));

        verify(quizQuestionService).update(eq(QUESTION_ID), any(QuizQuestionUpdateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_activate_question_when_requesting_activate() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{id}/activate", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isNoContent());

        verify(quizQuestionService).activate(QUESTION_ID, USER_ID);
    }

    @Test
    void should_deactivate_question_when_requesting_deactivate() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{id}/deactivate", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isNoContent());

        verify(quizQuestionService).deactivate(QUESTION_ID, USER_ID);
    }

    @Test
    void should_delete_question_when_requesting_delete() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(delete("/api/quizzes/{quizId}/questions/{id}", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isNoContent());

        verify(quizQuestionService).delete(QUESTION_ID, USER_ID);
    }

    @Test
    void should_restore_question_when_requesting_restore() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{id}/restore", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isNoContent());

        verify(quizQuestionService).restore(QUESTION_ID, USER_ID);
    }

    @Test
    void should_list_questions_by_quiz_when_requesting_quiz_questions() throws Exception {
        when(quizQuestionService.listByQuiz(QUIZ_ID)).thenReturn(List.of(quizQuestionResponse()));

        mockMvc.perform(get("/api/quizzes/{quizId}/questions", QUIZ_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].quizId").value(9L));

        verify(quizQuestionService).listByQuiz(QUIZ_ID);
    }
}