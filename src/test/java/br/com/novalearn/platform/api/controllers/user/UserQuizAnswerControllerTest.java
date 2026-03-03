package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerCreateRequestDTO;
import br.com.novalearn.platform.domain.services.user.UserQuizAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static br.com.novalearn.platform.factories.dtos.user.UserQuizAnswerTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserQuizAnswerController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserQuizAnswerControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserQuizAnswerService userQuizAnswerService;

    private static final Long ANSWER_ID = 12L;
    private static final Long QUESTION_ID = 20L;
    private static final Long QUIZ_ID = 9L;
    private static final Long OPTION_ID = 15L;

    @Test
    void should_answer_question_when_request_is_valid() throws Exception {
        UserQuizAnswerCreateRequestDTO request = userQuizAnswerCreateRequest();

        when(authService.getAuthenticatedUserId()).thenReturn(USER_ID);

        when(userQuizAnswerService.answerQuestion(
                any(UserQuizAnswerCreateRequestDTO.class), eq(USER_ID)))
                .thenReturn(userQuizAnswerResponse());

        mockMvc.perform(post("/api/me/quiz-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userQuizAnswerService).answerQuestion(
                any(UserQuizAnswerCreateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_change_answer_when_requesting_change() throws Exception {
        when(authService.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(userQuizAnswerService.changeAnswer(ANSWER_ID, OPTION_ID))
                .thenReturn(userQuizAnswerResponse());

        mockMvc.perform(patch("/api/me/quiz-answers/{id}/change-answer", ANSWER_ID)
                .param("optionId", OPTION_ID.toString()))
                .andExpect(status().isOk());

        verify(userQuizAnswerService).changeAnswer(ANSWER_ID, OPTION_ID);
    }

    @Test
    void should_return_answer_when_requesting_by_question() throws Exception {
        when(userQuizAnswerService.findMyAnswerByQuestion(QUESTION_ID))
                .thenReturn(userQuizAnswerResponse());

        mockMvc.perform(get("/api/me/quiz-answers/question/{questionId}", QUESTION_ID))
                .andExpect(status().isOk());

        verify(userQuizAnswerService).findMyAnswerByQuestion(QUESTION_ID);
    }

    @Test
    void should_list_answers_when_requesting_by_quiz() throws Exception {
        when(userQuizAnswerService.listMyAnswersByQuiz(QUIZ_ID))
                .thenReturn(List.of(userQuizAnswerListResponse()));

        mockMvc.perform(get("/api/me/quiz-answers/quiz/{quizId}", QUIZ_ID))
                .andExpect(status().isOk());

        verify(userQuizAnswerService).listMyAnswersByQuiz(QUIZ_ID);
    }
}