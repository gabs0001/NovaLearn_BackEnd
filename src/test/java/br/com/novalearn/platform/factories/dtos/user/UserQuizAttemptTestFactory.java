package br.com.novalearn.platform.factories.dtos.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptUpdateRequestDTO;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class UserQuizAttemptTestFactory {
    public static UserQuizAttemptCreateRequestDTO userQuizAttemptCreateRequest() {
        return new UserQuizAttemptCreateRequestDTO(9L);
    }

    public static UserQuizAttemptUpdateRequestDTO userQuizAttemptUpdateRequest() {
         return new UserQuizAttemptUpdateRequestDTO(
                 new BigDecimal("80.0"),
                 new BigDecimal("100.0"),
                 "New observations"
         );
    }

    public static UserQuizAttemptListResponseDTO userQuizAttemptListResponse() {
        return new UserQuizAttemptListResponseDTO(
                7L,
                9L,
                1,
                new BigDecimal("70.0"),
                true,
                QuizAttemptStatus.FINISHED,
                LocalDateTime.now().minusDays(2)
        );
    }

    public static UserQuizAttemptResponseDTO userQuizAttemptResponse() {
        return new UserQuizAttemptResponseDTO(
                7L,
                true,
                false,
                LocalDateTime.now().minusDays(7),
                null,
                "Observations",
                9L,
                1,
                new BigDecimal("70.0"),
                new BigDecimal("100.0"),
                QuizAttemptStatus.FINISHED,
                true,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2)
        );
    }
}