package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.domain.entities.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;
import static org.junit.jupiter.api.Assertions.*;

public class QuizValidateAttemptTest {
    private Module module;

    @BeforeEach
    void setUp() {
        module = createInitializedModule(createInitializedCourse());
    }

    private Quiz createQuizWithLimit(int maxAttempts) {
        Quiz quiz = Quiz.create(
                module,
                "Quiz",
                5,
                BigDecimal.TEN,
                maxAttempts,
                false
        );

        quiz.activate();
        quiz.markAsNotDeleted();

        return quiz;
    }

    @Test
    void should_allow_attempt_when_under_limit() {
        Quiz quiz = createQuizWithLimit(3);

        assertDoesNotThrow(() -> quiz.validateAttemptAllowed(2));
    }

    @Test
    void should_throw_when_limit_reached() {
        Quiz quiz = createQuizWithLimit(2);

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> quiz.validateAttemptAllowed(2)
        );

        assertEquals("Maximum number of attempts reached.", ex.getMessage());
    }

    @Test
    void should_throw_when_inactive() {
        Quiz quiz = createQuizWithLimit(3);
        quiz.deactivate();

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> quiz.validateAttemptAllowed(0)
        );

        assertEquals("Inactive quiz cannot be attempted.", ex.getMessage());
    }
}