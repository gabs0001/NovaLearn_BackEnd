package br.com.novalearn.platform.factories.entities.quiz;

import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;

public final class CreateQuestionFactory {
    private static final Quiz quiz = createInitializedQuiz();

    public static QuizQuestion createInitializedQuestion() {
        return QuizQuestion.create(
                quiz,
                1,
                "Some description...",
                10
        );
    }
}