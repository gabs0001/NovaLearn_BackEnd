package br.com.novalearn.platform.factories.dtos.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;

import java.time.LocalDateTime;

public final class UserQuizAnswerTestFactory {
    public static UserQuizAnswerCreateRequestDTO userQuizAnswerCreateRequest() {
        return new UserQuizAnswerCreateRequestDTO(5L, 20L, 15L);
    }

    public static UserQuizAnswerListResponseDTO userQuizAnswerListResponse() {
        return new UserQuizAnswerListResponseDTO(
                12L,
                5L,
                20L,
                15L,
                false,
                LocalDateTime.now().minusHours(4),
                true,
                false
        );
    }

    public static UserQuizAnswerResponseDTO userQuizAnswerResponse() {
        return new UserQuizAnswerResponseDTO(
                12L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                5L,
                20L,
                15L,
                false,
                LocalDateTime.now().minusHours(4),
                5L,
                null
        );
    }
}