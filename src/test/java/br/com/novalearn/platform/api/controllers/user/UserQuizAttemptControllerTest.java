package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptUpdateRequestDTO;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.domain.services.user.UserQuizAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.user.UserQuizAttemptTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserQuizAttemptController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserQuizAttemptControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserQuizAttemptService userQuizAttemptService;

    private static final Long ATTEMPT_ID = 7L;
    private static final Long QUIZ_ID = 9L;

    @BeforeEach
    void setUp() { mock_authenticated_user(); }

    @Test
    void should_list_my_attempts_when_requesting_list() throws Exception {
        when(userQuizAttemptService.listMyAttempts(USER_ID)).thenReturn(List.of(userQuizAttemptListResponse()));

        mockMvc.perform(get("/api/me/quiz-attempts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7L))
                .andExpect(jsonPath("$[0].quizId").value(9L))
                .andExpect(jsonPath("$[0].status").value("FINISHED"))
                .andExpect(jsonPath("$[0].passed").value(true));

        verify(userQuizAttemptService).listMyAttempts(USER_ID);
    }

    @Test
    void should_return_attempt_when_requesting_by_id() throws Exception {
        when(userQuizAttemptService.findMyAttemptById(ATTEMPT_ID, USER_ID))
                .thenReturn(userQuizAttemptResponse());

        mockMvc.perform(get("/api/me/quiz-attempts/{id}", ATTEMPT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.quizId").value(9L))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.passed").value(true));

        verify(userQuizAttemptService).findMyAttemptById(ATTEMPT_ID, USER_ID);
    }

    @Test
    void should_start_attempt_when_request_is_valid() throws Exception {
        UserQuizAttemptCreateRequestDTO request = userQuizAttemptCreateRequest();

        when(userQuizAttemptService.startAttempt(any(UserQuizAttemptCreateRequestDTO.class), eq(USER_ID)))
                .thenReturn(userQuizAttemptResponse());

        mockMvc.perform(post("/api/me/quiz-attempts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(9L));

        verify(userQuizAttemptService).startAttempt(any(UserQuizAttemptCreateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_finish_attempt_when_request_is_valid() throws Exception {
        UserQuizAttemptUpdateRequestDTO request = userQuizAttemptUpdateRequest();

        when(userQuizAttemptService.finishAttempt(
                eq(ATTEMPT_ID),
                any(UserQuizAttemptUpdateRequestDTO.class),
                eq(USER_ID)))
                .thenReturn(new UserQuizAttemptResponseDTO(
                        7L,
                        true,
                        false,
                        LocalDateTime.now().minusDays(7),
                        null,
                        "New observations",
                        9L,
                        1,
                        new BigDecimal("80.0"),
                        new BigDecimal("100.0"),
                        QuizAttemptStatus.FINISHED,
                        true,
                        LocalDateTime.now().minusDays(4),
                        LocalDateTime.now().minusDays(2)
                ));

        mockMvc.perform(patch("/api/me/quiz-attempts/{id}/finish", ATTEMPT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.observations").value("New observations"))
                .andExpect(jsonPath("$.score").value(80.0));

        verify(userQuizAttemptService)
                .finishAttempt(eq(ATTEMPT_ID), any(UserQuizAttemptUpdateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_delete_attempt_when_requesting_delete() throws Exception {
        mockMvc.perform(delete("/api/me/quiz-attempts/{id}", ATTEMPT_ID))
                .andExpect(status().isNoContent());

        verify(userQuizAttemptService).deleteMyAttempt(ATTEMPT_ID, USER_ID);
    }

    @Test
    void should_list_attempts_when_requesting_by_quiz() throws Exception {
        when(userQuizAttemptService.listMyAttemptsByQuiz(QUIZ_ID, USER_ID))
                .thenReturn(List.of(userQuizAttemptListResponse()));

        mockMvc.perform(get("/api/me/quiz-attempts/quiz/{quizId}/me", QUIZ_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7L))
                .andExpect(jsonPath("$[0].quizId").value(9L))
                .andExpect(jsonPath("$[0].status").value("FINISHED"))
                .andExpect(jsonPath("$[0].passed").value(true));

        verify(userQuizAttemptService).listMyAttemptsByQuiz(QUIZ_ID, USER_ID);
    }
}