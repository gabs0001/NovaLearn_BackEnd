package br.com.novalearn.platform.domain.entities.user.quizattempt;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateAttemptFactory.createInitializedAttempt;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAttemptCancelTest {
    private LocalDateTime now;
    private UserQuizAttempt attempt;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        attempt = createInitializedAttempt();
    }

    @Test
    void should_cancel_started_attempt() {
        attempt.cancel(1L, now);

        assertEquals(QuizAttemptStatus.CANCELLED, attempt.getStatus());
        assertEquals(now, attempt.getFinishedAt());
        assertFalse(attempt.isPassed());
    }

    @Test
    void should_fail_when_attempt_is_not_started() {
        attempt.finish(
                BigDecimal.ONE,
                BigDecimal.TEN,
                now,
                1L,
                now
        );

        assertThrows(
                InvalidStateException.class,
                () -> attempt.cancel(1L, now)
        );
    }

    @Test
    void should_fail_when_attempt_is_deleted() {
        attempt.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> attempt.cancel(1L, now)
        );
    }
}