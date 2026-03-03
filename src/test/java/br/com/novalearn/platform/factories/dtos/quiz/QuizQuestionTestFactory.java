package br.com.novalearn.platform.factories.dtos.quiz;

import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionUpdateRequestDTO;

import java.time.LocalDateTime;

public final class QuizQuestionTestFactory {
    public static QuizQuestionCreateRequestDTO quizQuestionCreateRequest() {
        return new QuizQuestionCreateRequestDTO(
                9L,
                1,
                "Some description",
                10,
                "Observations"
        );
    }

    public static QuizQuestionUpdateRequestDTO quizQuestionUpdateRequest() {
        return new QuizQuestionUpdateRequestDTO(
                2,
                "New description",
                5,
                "Observations"
        );
    }

    public static QuizQuestionListResponseDTO quizQuestionListResponse() {
        return new QuizQuestionListResponseDTO(
                20L,
                9L,
                1,
                "Some description",
                10,
                true,
                false
        );
    }

    public static QuizQuestionResponseDTO quizQuestionResponse() {
        return new QuizQuestionResponseDTO(
                20L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                9L,
                1,
                "Some description",
                10,
                5L,
                null
        );
    }
}