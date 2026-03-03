package br.com.novalearn.platform.factories.entities.quiz;

import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;

public final class CreateOptionFactory {
    public static QuizAnswerOption createInitializedOption(
            QuizQuestion question,
            LocalDateTime now
    ) {
        return QuizAnswerOption.create(
                question,
                1,
                "A",
                true,
                "obs",
                5L,
                now
        );
    }
}