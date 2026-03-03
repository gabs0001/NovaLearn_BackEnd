package br.com.novalearn.platform.domain.entities.user.quizattempt;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateAttemptFactory.createInitializedAttempt;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAttemptFinishTest {
    private LocalDateTime now;
    private UserQuizAttempt attempt;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        attempt = createInitializedAttempt();
    }

    @Test
    void should_finish_attempt_successfully() {
        BigDecimal score = new BigDecimal("7.0");
        BigDecimal max = new BigDecimal("10.0");

        LocalDateTime finish = now;

        attempt.finish(score, max, finish, 5L, now);

        assertEquals(QuizAttemptStatus.FINISHED, attempt.getStatus());
        assertEquals(score, attempt.getScore());
        assertEquals(max, attempt.getMaxScore());
        assertEquals(finish, attempt.getFinishedAt());
        assertTrue(attempt.isPassed());
    }

    @Test
    void should_pass_with_exactly_70_percent() {
        attempt.finish(
                new BigDecimal("7.0"),
                new BigDecimal("10.0"),
                now,
                5L,
                now
        );

        assertTrue(attempt.isPassed());
    }

    @Test
    void should_not_pass_with_less_than_70_percent() {
        attempt.finish(
                new BigDecimal("6.94"),
                new BigDecimal("10.0"),
                now,
                5L,
                now
        );

        assertFalse(attempt.isPassed());
    }

    @Test
    void should_fail_when_attempt_is_not_started() {
        attempt.cancel(5L, now);

        assertThrows(
                InvalidStateException.class,
                () -> attempt.finish(
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        now,
                        5L,
                        now
                )
        );
    }

    @Test
    void should_fail_when_score_is_negative() {
        assertThrows(
                ValidationException.class,
                () -> attempt.finish(
                        new BigDecimal("-1"),
                        BigDecimal.TEN,
                        now,
                        5L,
                        now
                )
        );
    }
}