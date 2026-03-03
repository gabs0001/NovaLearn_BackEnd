package br.com.novalearn.platform.factories.dtos.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionUpdateRequestDTO;

import java.time.LocalDateTime;

public final class QuizAnswerOptionTestFactory {
    public static QuizAnswerOptionCreateRequestDTO quizAnswerOptionCreateRequest() {
        return new QuizAnswerOptionCreateRequestDTO(
                20L,
                1,
                "A",
                false,
                "Observations"
        );
    }

    public static QuizAnswerOptionUpdateRequestDTO quizAnswerOptionUpdateRequest() {
        return new QuizAnswerOptionUpdateRequestDTO(
                1,
                "B",
                true,
                true,
                "Observations"
        );
    }

    public static QuizAnswerOptionListResponseDTO quizAnswerOptionListResponse() {
        return new QuizAnswerOptionListResponseDTO(
                15L,
                20L,
                1,
                "B",
                false,
                true,
                false
        );
    }

    public static QuizAnswerOptionResponseDTO quizAnswerOptionResponse() {
        return new QuizAnswerOptionResponseDTO(
                15L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                20L,
                1,
                "A",
                false,
                5L,
                null
        );
    }
}
