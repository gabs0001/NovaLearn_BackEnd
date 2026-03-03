package br.com.novalearn.platform.domain.entities.user.quizattempt;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.user.UserQuizAttempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.user.CreateAttemptFactory.createInitializedAttempt;
import static org.junit.jupiter.api.Assertions.*;

public class UserQuizAttemptValidationTest {
    private LocalDateTime now;
    private UserQuizAttempt attempt;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        attempt = createInitializedAttempt();
    }

    @Test
    void should_not_allow_operation_when_deleted() {
        attempt.markAsDeleted();

        assertThrows(
                ForbiddenOperationException.class,
                () -> attempt.ensureNotDeleted()
        );
    }

    @Test
    void should_not_finish_when_cancelled() {
        attempt.cancel(1L, now);

        assertThrows(
                InvalidStateException.class,
                () -> attempt.finish(
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        now,
                        1L,
                        now
                )
        );
    }
}