package br.com.novalearn.platform.factories.entities.user;

import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateAttemptFactory {
    private static final User user = createInitializedUser();
    private static final Quiz quiz = createInitializedQuiz();
    private static final LocalDateTime now = LocalDateTime.now();

    public static UserQuizAttempt createInitializedAttempt() {
        return UserQuizAttempt.start(
                user,
                quiz,
                1,
                1L,
                now
        );
    }
}