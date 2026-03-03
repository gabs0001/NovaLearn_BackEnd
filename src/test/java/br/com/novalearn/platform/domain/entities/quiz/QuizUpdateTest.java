package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.novalearn.platform.factories.entities.quiz.CreateQuizFactory.createInitializedQuiz;
import static org.junit.jupiter.api.Assertions.*;

public class QuizUpdateTest {
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = createInitializedQuiz();
    }

    @Test
    void should_update_fields() {
        quiz.update(
                "Updated",
                "Desc",
                "Instructions",
                10,
                new BigDecimal("80"),
                5,
                true,
                "Obs"
        );

        assertEquals("Updated", quiz.getName());
        assertEquals("Desc", quiz.getDescription());
        assertEquals("Instructions", quiz.getInstructions());
        assertEquals(10, quiz.getQtdQuestions());
        assertEquals(new BigDecimal("80"), quiz.getMinScore());
        assertEquals(5, quiz.getMaxAttempts());
        assertTrue(quiz.isRandomOrder());
        assertEquals("Obs", quiz.getObservations());
    }

    @Test
    void should_throw_when_deleted() {
        quiz.markAsDeleted();

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> quiz.update("X", null, null, null, null, null, null, null)
        );

        assertEquals("Deleted quiz cannot be modified.", ex.getMessage());
    }

    @Test
    void should_throw_when_inactive() {
        quiz.deactivate();

        InvalidStateException ex = assertThrows(
                InvalidStateException.class,
                () -> quiz.update("X", null, null, null, null, null, null, null)
        );

        assertEquals("Inactive quiz cannot be modified.", ex.getMessage());
    }

    @Test
    void should_throw_when_name_blank() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> quiz.update(
                        "   ",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );

        assertEquals("Quiz name cannot be blank.", ex.getMessage());
    }
}