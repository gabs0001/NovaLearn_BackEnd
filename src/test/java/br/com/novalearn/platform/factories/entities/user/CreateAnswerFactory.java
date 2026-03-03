package br.com.novalearn.platform.factories.entities.user;

import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateOptionFactory.createInitializedOption;
import static br.com.novalearn.platform.factories.entities.quiz.CreateQuestionFactory.createInitializedQuestion;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateAnswerFactory {
    public static UserQuizAnswer createInitializedAnswer(
            User user,
            QuizQuestion question,
            QuizAnswerOption option,
            LocalDateTime now
    ) {
        return UserQuizAnswer.answer(
                user,
                question,
                option,
                now,
                now
        );
    }
}