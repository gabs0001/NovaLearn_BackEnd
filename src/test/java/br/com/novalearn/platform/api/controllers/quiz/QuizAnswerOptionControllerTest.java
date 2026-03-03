package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.quiz.QuizAnswerOptionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.quiz.QuizAnswerOptionTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizAnswerOptionController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class QuizAnswerOptionControllerTest extends BaseControllerTest {
    @MockitoBean
    private QuizAnswerOptionService quizAnswerOptionService;

    private static final Long OPTION_ID = 15L;
    private static final Long QUESTION_ID = 20L;
    private static final Long QUIZ_ID = 9L;

    @Test
    void should_return_option_when_id_exists() throws Exception {
        when(quizAnswerOptionService.findById(OPTION_ID)).thenReturn(quizAnswerOptionResponse());

        mockMvc.perform(get("/api/quizzes/{quizId}/questions/{questionId}/options/{id}", QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15L))
                .andExpect(jsonPath("$.correct").value(false));

        verify(quizAnswerOptionService).findById(OPTION_ID);
    }

    @Test
    void should_create_option_when_request_is_valid() throws Exception {
        QuizAnswerOptionCreateRequestDTO request = quizAnswerOptionCreateRequest();

        mock_authenticated_user();
        when(quizAnswerOptionService.create(eq(USER_ID), any())).thenReturn(quizAnswerOptionResponse());

        mockMvc.perform(post("/api/quizzes/{quizId}/questions/{questionId}/options", QUIZ_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(20L));

        verify(quizAnswerOptionService).create(eq(USER_ID), any(QuizAnswerOptionCreateRequestDTO.class));
    }

    @Test
    void should_update_option_when_request_is_valid() throws Exception {
        QuizAnswerOptionUpdateRequestDTO request = quizAnswerOptionUpdateRequest();

        mock_authenticated_user();
        when(quizAnswerOptionService.update(eq(OPTION_ID), any(), eq(USER_ID)))
                .thenReturn(new QuizAnswerOptionResponseDTO(
                        15L,
                        true,
                        false,
                        LocalDateTime.now(),
                        null,
                        "Observations",
                        20L,
                        1,
                        "B",
                        true,
                        5L,
                        null
                ));

        mockMvc.perform(
                patch("/api/quizzes/{quizId}/questions/{questionId}/options/{id}", QUIZ_ID, QUESTION_ID, OPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.option").value("B"))
                .andExpect(jsonPath("$.correct").value(true));

        verify(quizAnswerOptionService)
                .update(eq(OPTION_ID), any(QuizAnswerOptionUpdateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_activate_option_when_requesting_activate() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(
                patch("/api/quizzes/{quizId}/questions/{questionId}/options/{id}/activate",
                        QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).activate(OPTION_ID, USER_ID);
    }

    @Test
    void should_deactivate_option_when_requesting_deactivate() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(
                patch("/api/quizzes/{quizId}/questions/{questionId}/options/{id}/deactivate",
                        QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).deactivate(OPTION_ID, USER_ID);
    }

    @Test
    void should_delete_option_when_requesting_delete() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(
                delete("/api/quizzes/{quizId}/questions/{questionId}/options/{id}",
                        QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).delete(OPTION_ID, USER_ID);
    }

    @Test
    void should_restore_option_when_requesting_restore() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(
                patch("/api/quizzes/{quizId}/questions/{questionId}/options/{id}/restore",
                        QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).restore(OPTION_ID, USER_ID);
    }

    @Test
    void should_list_options_by_question_when_requesting_question_options() throws Exception {
        when(quizAnswerOptionService.listByQuestion(QUESTION_ID)).thenReturn(List.of(quizAnswerOptionResponse()));

        mockMvc.perform(get("/api/quizzes/{quizId}/questions/{questionId}/options", QUIZ_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15L))
                .andExpect(jsonPath("$[0].questionId").value(20L))
                .andExpect(jsonPath("$[0].correct").value(false));

        verify(quizAnswerOptionService).listByQuestion(QUESTION_ID);
    }

    @Test
    void should_mark_option_as_correct_when_requesting_mark_correct() throws Exception {
        mock_authenticated_user();

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{questionId}/options/{id}/mark-correct",
                        QUIZ_ID, QUESTION_ID, OPTION_ID))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).markAsCorrect(OPTION_ID, USER_ID);
    }

    @Test
    void should_reorder_options_when_requesting_reorder() throws Exception {
        List<Long> orderedIds = List.of(1L, 2L, 3L);

        mock_authenticated_user();

        mockMvc.perform(patch("/api/quizzes/{quizId}/questions/{questionId}/options/reorder", QUIZ_ID, QUESTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderedIds)))
                .andExpect(status().isNoContent());

        verify(quizAnswerOptionService).reorder(eq(orderedIds), eq(USER_ID));
    }
}