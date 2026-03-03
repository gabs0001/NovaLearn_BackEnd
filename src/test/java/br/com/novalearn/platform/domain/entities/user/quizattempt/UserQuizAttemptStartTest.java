package br.com.novalearn.platform.domain.entities.user.quizattempt;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAttemptStartTest {
    private LocalDateTime now;
    private User user;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = createInitializedUser();
        quiz = createInitializedQuiz();
    }

    @Test
    void should_start_attempt_successfully() {
        UserQuizAttempt attempt = UserQuizAttempt.start(user, quiz, 1, 10L, now);

        assertNotNull(attempt);
        assertEquals(user, attempt.getUser());
        assertEquals(quiz, attempt.getQuiz());
        assertEquals(1, attempt.getSeqAttempt());
        assertEquals(QuizAttemptStatus.STARTED, attempt.getStatus());
        assertFalse(attempt.isPassed());
        assertEquals(now, attempt.getStartedAt());
    }

    @Test
    void should_fail_when_user_is_null() {
        assertThrows(
                ValidationException.class,
                () -> UserQuizAttempt.start(null, quiz, 1, 1L, now)
        );
    }

    @Test
    void should_fail_when_quiz_is_null() {
        assertThrows(
                ValidationException.class,
                () -> UserQuizAttempt.start(user, null, 1, 1L, now)
        );
    }

    @Test
    void should_fail_when_sequence_is_invalid() {
        assertThrows(
                ValidationException.class,
                () -> UserQuizAttempt.start(user, quiz, 0, 1L, now)
        );
    }

    @Test
    void should_fail_when_start_date_is_null() {
        assertThrows(
                ValidationException.class,
                () -> UserQuizAttempt.start(user, quiz, 1, 1L, null)
        );
    }
}