package br.com.novalearn.platform.factories.dtos.quiz;

import br.com.novalearn.platform.api.dtos.quiz.*;
import br.com.novalearn.platform.api.dtos.quiz.summary.QuizSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class QuizTestFactory {
    public static QuizCreateRequestDTO quizCreateRequest() {
        return new QuizCreateRequestDTO(
                6L,
                "First Quiz",
                "This is the first quiz",
                "Here are some instructions for the quiz",
                10,
                new BigDecimal("70.0"),
                3,
                true
        );
    }

    public static QuizUpdateRequestDTO quizUpdateRequest() {
        return new QuizUpdateRequestDTO(
                "New Quiz",
                "This is the new quiz",
                "Here are some instructions for the quiz",
                15,
                new BigDecimal("70.0"),
                3,
                true,
                "Observations"
        );
    }

    public static QuizResponseDTO quizResponse() {
        return new QuizResponseDTO(
                9L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                6L,
                "First Quiz",
                "This is the first quiz",
                "Here are some instructions for the quiz",
                10,
                new BigDecimal("70.0"),
                3,
                true,
                5L,
                null
        );
    }

    public static QuizSummaryResponseDTO quizSummaryResponse() {
        return new QuizSummaryResponseDTO(
                9L,
                "First Quiz",
                10,
                8,
                6,
                new BigDecimal("70.0"),
                new BigDecimal("40.0"),
                true,
                2,
                3
        );
    }

    public static UserQuizAttemptListResponseDTO userQuizAttemptListResponse() {
        return new UserQuizAttemptListResponseDTO(
                5L,
                9L,
                1,
                new BigDecimal("70.0"),
                true,
                QuizAttemptStatus.STARTED,
                null
        );
    }
}